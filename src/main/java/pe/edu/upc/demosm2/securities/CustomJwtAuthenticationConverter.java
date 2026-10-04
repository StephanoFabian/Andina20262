package org.example.andina2026.securities;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Traduce un token JWT ya validado a una "identidad" de Spring Security con sus permisos.
 *
 * ¿Cuándo se llama? En CADA petición con token: el Resource Server valida la firma con el
 * decoder (JwtConfig) y luego pasa el token por aquí para sacar QUÉ roles tiene el usuario.
 * Esos roles (authorities) son los que después revisan las anotaciones @PreAuthorize de los
 * controllers, p.ej. @PreAuthorize("hasRole('ADMIN')").
 *
 * Es un @Component: así lo gestiona Spring y se inyecta en SecurityConfig (antes se creaba
 * con "new"). Lee el claim "roles" como LISTA, que es como lo guarda ahora JwtTokenService.
 */
@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        // "roles" viaja dentro del token como lista de textos, p.ej. ["ROLE_ADMIN"]
        List<String> roles = jwt.getClaimAsStringList("roles");

        List<SimpleGrantedAuthority> authorities = (roles == null)
                ? List.of()
                : roles.stream()
                        .filter(role -> role != null && !role.isBlank())
                        .map(String::trim)
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }
}
