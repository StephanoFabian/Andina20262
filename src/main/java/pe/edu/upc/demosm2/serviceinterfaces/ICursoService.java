package pe.edu.upc.demosm2.serviceinterfaces;

import pe.edu.upc.demosm2.entities.Curso;

import java.util.List;
import java.util.Optional;

public interface ICursoService {
    public void insert(Curso q);
    public List<Curso> list();
    public Optional<Curso> listId(Long id);
    public void update(Curso q);
    public void delete(Long id);
    public List<Curso> buscarporArea(String area);
}
