package pe.edu.upc.demosm2.serviceinterfaces;

import pe.edu.upc.demosm2.entities.Grado;

import java.util.List;
import java.util.Optional;

public interface GradoServiceInterface {
    public List<Grado> list();
    public void insert(Grado grado);
    public Optional<Grado> listId(Long id);
    public void update(Grado grado);
    public void delete(Long id);
}
