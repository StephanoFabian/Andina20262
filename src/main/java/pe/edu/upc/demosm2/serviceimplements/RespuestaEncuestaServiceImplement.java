package pe.edu.upc.demosm2.serviceimplements;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.demosm2.entities.RespuestaEncuesta;
import pe.edu.upc.demosm2.repositories.IRespuestaEncuestaRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IRespuestaEncuestaService;

@Service
public class RespuestaEncuestaServiceImplement implements IRespuestaEncuestaService {

    @Autowired
    private IRespuestaEncuestaRepository reR;

    @Override
    public List<RespuestaEncuesta> list() {
        return reR.findAll();
    }

    @Override
    public RespuestaEncuesta insert(RespuestaEncuesta x) {
        return reR.save(x);
    }

    @Override
    public Optional<RespuestaEncuesta> listId(Long id) {
        return reR.findById(id);
    }

    @Override
    public void delete(Long id) {
        reR.deleteById(id);
    }

    @Override
    public List<RespuestaEncuesta> listarPorEncuesta(Long idEncuesta) {
        return reR.listarPorEncuesta(idEncuesta);
    }

    @Override
    public long contarRespuestas(Long idEncuesta, Long idPersona) {
        return reR.contarRespuestas(idEncuesta, idPersona);
    }

    @Override
    public List<Object[]> reporteSatisfaccionPorRol(Long idEncuesta) {
        return reR.reporteSatisfaccionPorRol(idEncuesta);
    }
}
