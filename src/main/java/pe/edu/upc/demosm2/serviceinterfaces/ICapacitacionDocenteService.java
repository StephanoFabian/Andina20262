package pe.edu.upc.demosm2.serviceinterfaces;

import java.util.List;
import java.util.Optional;
import pe.edu.upc.demosm2.entities.CapacitacionDocente;

public interface ICapacitacionDocenteService {
    public List<CapacitacionDocente> list();

    public CapacitacionDocente insert(CapacitacionDocente x);

    public Optional<CapacitacionDocente> listId(Long id);

    public void update(CapacitacionDocente x);

    public void delete(Long id);

    public List<Object[]> reportePreparacionPorColegio();
}
