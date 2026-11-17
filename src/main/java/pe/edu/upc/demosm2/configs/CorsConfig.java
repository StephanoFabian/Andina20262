package pe.edu.upc.demosm2.configs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
@Configuration
public class CorsConfig {
    // Orígenes permitidos separados por coma (variable ANDINA_CORS_ORIGINS); por defecto el front local.
    @Value("${ANDINA_CORS_ORIGINS:http://localhost:4200,http://127.0.0.1:4200}")
    private List<String> origenes;
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration=new CorsConfiguration();
        configuration.setAllowedOrigins(origenes);
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization","Location"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }
}
