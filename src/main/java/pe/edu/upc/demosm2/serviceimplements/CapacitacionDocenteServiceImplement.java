package pe.edu.upc.demosm2.serviceimplements;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.demosm2.entities.CapacitacionDocente;
import pe.edu.upc.demosm2.repositories.ICapacitacionDocenteRepository;
import pe.edu.upc.demosm2.serviceinterfaces.ICapacitacionDocenteService;

@Service
public class CapacitacionDocenteServiceImplement implements ICapacitacionDocenteService {

    @Autowired
    private ICapacitacionDocenteRepository cdR;

    @Override
    public List<CapacitacionDocente> list() {
        return cdR.findAll();
    }

    @Override
    public CapacitacionDocente insert(CapacitacionDocente x) {
        return cdR.save(x);
    }

    @Override
    public Optional<CapacitacionDocente> listId(Long id) {
        return cdR.findById(id);
    }

    @Override
    public void update(CapacitacionDocente x) {
        cdR.save(x);
    }

    @Override
    public void delete(Long id) {
        cdR.deleteById(id);
    }

    @Override
    public List<Object[]> reportePreparacionPorColegio() {
        return cdR.reportePreparacionPorColegio();
    }
}
