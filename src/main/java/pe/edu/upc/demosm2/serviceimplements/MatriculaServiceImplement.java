package pe.edu.upc.demosm2.serviceimplements;

import org.springframework.stereotype.Service;
import pe.edu.upc.demosm2.entities.Matricula;
import pe.edu.upc.demosm2.repositories.IMatriculaRepository;
import pe.edu.upc.demosm2.serviceinterfaces.MatriculaServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class MatriculaServiceImplement implements MatriculaServiceInterface {
    private final IMatriculaRepository repository;

    public MatriculaServiceImplement(IMatriculaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Matricula> list() {
        return repository.findAll();
    }

    @Override
    public void insert(Matricula matricula) {
        repository.save(matricula);
    }

    @Override
    public Optional<Matricula> listId(Long id) {
        return repository.findById(id);
    }

    @Override
    public void update(Matricula matricula) {
        repository.save(matricula);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Object[]> matriculasPorColegio() {
        return repository.matriculasPorColegio();
    }

    @Override
    public List<Object[]> estudiantesConVariasMatriculas() {
        return repository.estudiantesConVariasMatriculas();
    }

    @Override
    public long contarDuplicadas(Long idPersona, Long idColegio, Long excluir) {
        return repository.contarDuplicadas(idPersona, idColegio, excluir);
    }
}
