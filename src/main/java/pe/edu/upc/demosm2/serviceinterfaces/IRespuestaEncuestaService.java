package pe.edu.upc.demosm2.serviceinterfaces;

import java.util.List;
import java.util.Optional;
import pe.edu.upc.demosm2.entities.RespuestaEncuesta;

public interface IRespuestaEncuestaService {
    public List<RespuestaEncuesta> list();

    public RespuestaEncuesta insert(RespuestaEncuesta x);

    public Optional<RespuestaEncuesta> listId(Long id);

    public void delete(Long id);

    public List<RespuestaEncuesta> listarPorEncuesta(Long idEncuesta);

    public long contarRespuestas(Long idEncuesta, Long idPersona);

    public List<Object[]> reporteSatisfaccionPorRol(Long idEncuesta);
}
