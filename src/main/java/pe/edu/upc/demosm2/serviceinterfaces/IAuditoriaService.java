package pe.edu.upc.demosm2.serviceinterfaces;

import pe.edu.upc.demosm2.entities.Auditoria;

import java.time.LocalDateTime;
import java.util.List;

public interface IAuditoriaService {
    // HU15: agrega un registro a la bitácora (nunca lanza excepción: un fallo de bitácora no corta la petición)
    public void registrar(String usuario, String modulo, String accion, String detalle, String ip, Integer estadoHttp);

    public List<Auditoria> filtrar(String usuario, String modulo, String accion, LocalDateTime desde, LocalDateTime hasta,
                                   int page, int size);

    public List<Object[]> reporteLoginFallidos(Integer dias);
}
