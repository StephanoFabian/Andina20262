package pe.edu.upc.demosm2.securities;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import pe.edu.upc.demosm2.serviceinterfaces.IAuditoriaService;

/**
 * HU15: deja en la bitácora (tabla auditoria) toda operación que modifica datos: POST, PUT, PATCH y DELETE.
 * Guarda quién la hizo (ID de persona del token), el módulo, la acción, la ruta, la IP y el código HTTP final,
 * incluidos los intentos rechazados (403). Las consultas GET no se registran. El login se registra aparte en
 * JwtAuthenticationController, porque ahí se conoce el ID con el que se intentó entrar.
 */
@Component
public class AuditoriaInterceptor implements HandlerInterceptor {

    @Autowired
    private IAuditoriaService auditoria;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String metodo = request.getMethod();
        String accion = switch (metodo) {
            case "POST" -> "CREAR";
            case "PUT", "PATCH" -> "ACTUALIZAR";
            case "DELETE" -> "ELIMINAR";
            default -> null;
        };
        String ruta = request.getRequestURI();
        if (accion == null || ruta.equals("/login")) return;
        int estado = response.getStatus();
        if (ex instanceof AccessDeniedException) estado = 403;
        else if (ex != null && estado < 400) estado = 500;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = auth == null ? null : auth.getName();
        auditoria.registrar(usuario, modulo(ruta), accion, metodo + " " + ruta, request.getRemoteAddr(), estado);
    }

    // "/api/colegios/5" -> "COLEGIOS"; "/personas/nuevo" -> "PERSONAS"
    private static String modulo(String ruta) {
        String[] partes = ruta.replaceFirst("^/api/", "/").split("/");
        return partes.length > 1 && !partes[1].isBlank() ? partes[1].toUpperCase() : "RAIZ";
    }
}
