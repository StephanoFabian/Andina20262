package pe.edu.upc.demosm2.serviceinterfaces;

import pe.edu.upc.demosm2.entities.Matricula;

import java.util.List;
import java.util.Optional;

public interface MatriculaServiceInterface {
    public List<Matricula> list();
    public void insert(Matricula matricula);
    public Optional<Matricula> listId(Long id);
    public void update(Matricula matricula);
    public void delete(Long id);

    public List<Object[]> matriculasPorColegio();
    public List<Object[]> estudiantesConVariasMatriculas();

    public long contarDuplicadas(Long idPersona, Long idColegio, Long excluir);
}
