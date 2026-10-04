package org.example.andina2026.securities;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuración del JWT, leída desde application.properties con el prefijo "jwt".
 *
 * Antes el secreto se leía con @Value suelto y la duración estaba escrita "a mano" dentro del
 * código. Aquí quedan los dos en un solo lugar, tipados y fáciles de cambiar por entorno:
 *   jwt.secret            -> clave secreta con la que se FIRMA y se VALIDA el token (HMAC-SHA512)
 *   jwt.expiration-hours  -> horas que dura la sesión antes de caducar (por defecto 8, Word H2.1)
 *
 * Spring rellena estos campos solo; se activa con @EnableConfigurationProperties en JwtConfig.
 */
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** Clave secreta para firmar el token. NUNCA debe subirse a Git ni compartirse. */
    private String secret;

    /** Duración de la sesión en horas. Valor por defecto si no se define en properties. */
    private int expirationHours = 8;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getExpirationHours() {
        return expirationHours;
    }

    public void setExpirationHours(int expirationHours) {
        this.expirationHours = expirationHours;
    }
}
