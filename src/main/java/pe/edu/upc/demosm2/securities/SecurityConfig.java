package org.example.andina2026.securities;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Centro de la seguridad. Aquí se define QUIÉN puede entrar y CÓMO se comprueban las claves.
 *
 * ================= CÓMO SE LLAMA A LA SEGURIDAD (el recorrido de una petición) =================
 *
 *  1) LOGIN (una sola vez):
 *     POST /login  ->  LoginController pide a AuthenticationManager que verifique DNI + contraseña.
 *                      AuthenticationManager usa el authenticationProvider() de abajo, que:
 *                        - busca al usuario con JwtUserDetailsService (UserDetailsService), y
 *                        - compara la contraseña con BCrypt (ver "HASHEO" más abajo).
 *                      Si todo cuadra, JwtTokenService crea un token JWT y se lo devuelve al usuario.
 *
 *  2) CADA PETICIÓN posterior lleva la cabecera "Authorization: Bearer <token>":
 *        - El Resource Server (oauth2ResourceServer, más abajo) valida la firma del token con el
 *          jwtDecoder (JwtConfig).
 *        - CustomJwtAuthenticationConverter saca los roles del token y los deja como "authorities".
 *        - Las reglas de authorizeHttpRequests y las anotaciones @PreAuthorize("hasRole('...')")
 *          de los controllers deciden si ese usuario puede o no ejecutar la acción.
 *
 *  Nota: es STATELESS (sin sesión en el servidor). El único "recuerdo" del login es el token que
 *        guarda el cliente; por eso @EnableMethodSecurity activa el control por @PreAuthorize.
 *
 * ============================= HASHEO DE CONTRASEÑAS (BCrypt) ==================================
 *
 *  - passwordEncoder() define BCrypt como forma de cifrar contraseñas (hash de una sola vía:
 *    del hash NO se puede volver a la contraseña original).
 *  - Al REGISTRAR un usuario (UsersController) se llama a passwordEncoder.encode(clave) y se guarda
 *    en la base de datos SOLO el hash, nunca la contraseña en claro.
 *  - Al INICIAR SESIÓN, el authenticationProvider() usa el mismo BCrypt para comparar la contraseña
 *    escrita con el hash guardado (encoder.matches). BCrypt añade "sal" automática, así que dos
 *    usuarios con la misma contraseña tienen hashes distintos.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final CustomJwtAuthenticationConverter jwtAuthenticationConverter;

    public SecurityConfig(UserDetailsService userDetailsService,
                          CustomJwtAuthenticationConverter jwtAuthenticationConverter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    /** Algoritmo de hasheo de contraseñas: BCrypt (con sal automática). */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provider que valida el login: busca al usuario (userDetailsService) y compara su
     * contraseña con el hash BCrypt guardado. Lo usa el AuthenticationManager.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /** El AuthenticationManager es a quien LoginController le pide "verifica este DNI y clave". */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    /** Reglas de acceso: qué es público y qué exige token válido. */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                // Sin CSRF y sin sesión: la API es sin estado, la identidad viaja en el token.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Login: público (aún no hay token)
                        .requestMatchers("/login").permitAll()
                        // Documentación Swagger: pública
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        // Peticiones OPTIONS (CORS): públicas
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Todo lo demás exige token válido
                        .anyRequest().authenticated()
                )

                // Modo "Resource Server": valida el token y, con nuestro converter, saca los roles.
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                );

        return http.build();
    }
}
