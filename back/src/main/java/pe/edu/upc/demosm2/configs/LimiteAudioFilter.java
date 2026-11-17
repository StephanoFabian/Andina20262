package pe.edu.upc.demosm2.configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/** Limita el cuerpo antes de deserializar base64 en memoria. */
@Component
public class LimiteAudioFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        boolean material = request.getRequestURI().equals("/api/materiales-ia");
        if (request.getMethod().equals("POST") && (material || request.getRequestURI().matches("/api/sesiones-virtuales/[0-9]+/videollamada/fragmentos"))) {
            long length = request.getContentLengthLong();
            if (length < 0 || length > (material ? 20_000 : 1_410_000)) {
                response.setStatus(length < 0 ? 411 : 413);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"message\":\"Envía un cuerpo con Content-Length dentro de los límites del servicio\"}");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
