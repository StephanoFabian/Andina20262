package pe.edu.upc.demosm2.serviceinterfaces;
import pe.edu.upc.demosm2.entities.Colegio;

import java.util.List;
import java.util.Optional;

public interface ColegioServiceInterface {

    public List<Colegio> list();
    public void insert(Colegio c);
    public Optional<Colegio> listId(Long id);
    public void delete(Long id);
}
