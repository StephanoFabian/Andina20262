package org.example.andina2026.securities;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Piezas para crear (encoder) y validar (decoder) los tokens JWT.
 *
 * Cómo se usan:
 *   - El ENCODER lo usa JwtTokenService para firmar el token cuando alguien inicia sesión.
 *   - El DECODER lo usa el Resource Server de Spring (configurado en SecurityConfig) para
 *     comprobar la firma de cada token que llega en la cabecera "Authorization: Bearer ...".
 *
 * La misma clave secreta (jwt.secret) firma y valida: por eso el algoritmo es simétrico (HMAC).
 * @EnableConfigurationProperties activa JwtProperties para poder inyectar aquí el secreto.
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    private final JwtProperties jwtProperties;

    public JwtConfig(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /** Convierte el texto del secreto en una clave HMAC-SHA512. */
    @Bean
    public SecretKey jwtSecretKey() {
        return new SecretKeySpec(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8),
                "HmacSHA512"
        );
    }

    /** Firma tokens nuevos (lo llama JwtTokenService al hacer login). */
    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    /** Valida la firma de los tokens entrantes (lo usa el Resource Server en cada petición). */
    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey) {
        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }
}
