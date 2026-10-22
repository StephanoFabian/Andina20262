package pe.edu.upc.demosm2.serviceimplements;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.demosm2.entities.Asistencia;
import pe.edu.upc.demosm2.repositories.IAsistenciaRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IAsistenciaService;

@Service
public class AsistenciaServiceImplement implements IAsistenciaService {

    @Autowired
    private IAsistenciaRepository aR;

    @Override
    public List<Asistencia> list() {
        return aR.findAll();
    }

    @Override
    public Asistencia insert(Asistencia x) {
        return aR.save(x);
    }

    @Override
    public Optional<Asistencia> listId(Long id) {
        return aR.findById(id);
    }

    @Override
    public void update(Asistencia x) {
        aR.save(x);
    }

    @Override
    public void delete(Long id) {
        aR.deleteById(id);
    }

    @Override
    public List<Asistencia> listarPorPersona(Long idPersona) {
        return aR.listarPorPersona(idPersona);
    }

    @Override
    public List<Object[]> reporteParticipacion(Long idPeriodo) {
        return aR.reporteParticipacion(idPeriodo);
    }
}
