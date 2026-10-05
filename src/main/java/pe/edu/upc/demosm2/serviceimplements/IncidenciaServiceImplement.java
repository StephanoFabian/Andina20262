package pe.edu.upc.demosm2.serviceimplements;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.demosm2.entities.Incidencia;
import pe.edu.upc.demosm2.repositories.IIncidenciaRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IIncidenciaService;

@Service
public class IncidenciaServiceImplement implements IIncidenciaService {

    @Autowired
    private IIncidenciaRepository iR;

    @Override
    public List<Incidencia> list() {
        return iR.findAll();
    }

    @Override
    public Incidencia insert(Incidencia x) {
        return iR.save(x);
    }

    @Override
    public Optional<Incidencia> listId(Long id) {
        return iR.findById(id);
    }

    @Override
    public void update(Incidencia x) {
        iR.save(x);
    }

    @Override
    public void delete(Long id) {
        iR.deleteById(id);
    }

    @Override
    public List<Incidencia> listarPorPersona(Long idPersona) {
        return iR.listarPorPersona(idPersona);
    }

    @Override
    public List<Incidencia> listarCriticasPendientes() {
        return iR.listarCriticasPendientes();
    }

    @Override
    public List<Object[]> reportePorPrioridad() {
        return iR.reportePorPrioridad();
    }
}
