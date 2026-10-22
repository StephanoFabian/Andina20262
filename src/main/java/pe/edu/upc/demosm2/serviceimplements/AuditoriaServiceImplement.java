package pe.edu.upc.demosm2.serviceimplements;

import pe.edu.upc.demosm2.entities.Auditoria;
import pe.edu.upc.demosm2.repositories.IAuditoriaRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IAuditoriaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditoriaServiceImplement implements IAuditoriaService {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaServiceImplement.class);

    @Autowired
    private IAuditoriaRepository aR;

    @Override
    public void registrar(String usuario, String modulo, String accion, String detalle, String ip, Integer estadoHttp) {
        try {
            Auditoria a = new Auditoria();
            a.setFechaHora(LocalDateTime.now());
            a.setUsuario(recortar(usuario, 50));
            a.setModulo(recortar(modulo, 50));
            a.setAccion(recortar(accion, 30));
            a.setDetalle(recortar(detalle, 300));
            a.setIp(recortar(ip, 45));
            a.setEstadoHttp(estadoHttp);
            aR.save(a);
        } catch (RuntimeException e) {
            log.error("No se pudo guardar el registro de auditoría {} {}: {}", modulo, accion, e.getMessage());
        }
    }

    @Override
    public List<Auditoria> filtrar(String usuario, String modulo, String accion, LocalDateTime desde, LocalDateTime hasta,
                                   int page, int size) {
        // Sin fechas se consulta todo el rango (PostgreSQL no puede tipar un parámetro de fecha nulo)
        LocalDateTime inicio = desde == null ? LocalDateTime.of(2000, 1, 1, 0, 0) : desde;
        LocalDateTime fin = hasta == null ? LocalDateTime.of(9999, 12, 31, 23, 59) : hasta;
        return aR.filtrar(usuario, modulo, accion, inicio, fin, PageRequest.of(page, size));
    }

    @Override
    public List<Object[]> reporteLoginFallidos(Integer dias) {
        return aR.reporteLoginFallidos(dias);
    }

    private static String recortar(String valor, int max) {
        return valor == null || valor.length() <= max ? valor : valor.substring(0, max);
    }
}
