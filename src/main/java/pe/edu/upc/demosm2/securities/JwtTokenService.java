package org.example.andina2026.securities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Fabrica el token JWT que el usuario usará como "pase" en cada petición.
 *
 * ¿Quién lo llama? LoginController, DESPUÉS de que AuthenticationManager haya comprobado
 * el DNI y la contraseña. Es decir, aquí el usuario ya está verificado: solo se le entrega
 * el token firmado con sus datos y sus roles.
 *
 * Dentro del token se guardan:
 *   - subject : el username del usuario.
 *   - roles   : la LISTA de roles (p.ej. ["ROLE_ADMIN"]). Se guarda como lista, no como
 *               texto con comas, para que un rol no se rompa si algún día lleva una coma.
 *   - expiresAt: cuándo caduca (jwt.expiration-hours, por defecto 8 h).
 * La firma la pone el JwtEncoder con la clave secreta (ver JwtConfig).
 */
@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public JwtTokenService(JwtEncoder jwtEncoder, JwtProperties jwtProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(UserDetails userDetails) {

        Instant now = Instant.now();
        Duration validity = Duration.ofHours(jwtProperties.getExpirationHours());

        // Roles del usuario ya autenticado -> lista de textos (p.ej. "ROLE_ADMIN")
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(validity))
                .claim("roles", roles)          // lista, la lee CustomJwtAuthenticationConverter
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS512)
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }
}
