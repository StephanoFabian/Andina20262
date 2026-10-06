package pe.edu.upc.demosm2.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pe.edu.upc.demosm2.securities.AuditoriaInterceptor;

// Registra el interceptor de la bitácora (HU15) para todas las rutas de la API
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuditoriaInterceptor auditoriaInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditoriaInterceptor);
    }
}
