package pe.edu.upc.demosm2.serviceimplements;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import pe.edu.upc.demosm2.entities.PeriodoAcademico;
import pe.edu.upc.demosm2.repositories.IPeriodoAcademicoRepository;
import pe.edu.upc.demosm2.serviceinterfaces.PeriodoAcademicoServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class PeriodoAcademicoServiceImplement implements PeriodoAcademicoServiceInterface {
    private final IPeriodoAcademicoRepository repository;

    public PeriodoAcademicoServiceImplement(IPeriodoAcademicoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PeriodoAcademico> list() {
        return repository.findAll();
    }

    @Override
    public void insert(PeriodoAcademico periodoAcademico) {
        repository.save(periodoAcademico);
    }

    @Override
    public Optional<PeriodoAcademico> listId(Long id) {
        return repository.findById(id);
    }

    @Override
    public void update(PeriodoAcademico periodoAcademico) {
        repository.save(periodoAcademico);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Object[]> evolucionDeMatricula() {
        return repository.evolucionDeMatricula();
    }

    @Override
    public List<Object[]> matriculaTardiaPorPeriodo() {
        return repository.matriculaTardiaPorPeriodo();
    }

    @Override
    public long contarCruces(LocalDate inicio, LocalDate fin, Long excluir) {
        return repository.contarCruces(inicio, fin, excluir);
    }

    @Override
    public long contarActivos(Long excluir) {
        return repository.contarActivos(excluir);
    }
}
