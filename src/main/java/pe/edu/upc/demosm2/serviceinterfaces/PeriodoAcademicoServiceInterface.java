package pe.edu.upc.demosm2.serviceinterfaces;

import java.time.LocalDate;

import pe.edu.upc.demosm2.entities.PeriodoAcademico;

import java.util.List;
import java.util.Optional;

public interface PeriodoAcademicoServiceInterface {
    public List<PeriodoAcademico> list();
    public void insert(PeriodoAcademico periodoAcademico);
    public Optional<PeriodoAcademico> listId(Long id);
    public void update(PeriodoAcademico periodoAcademico);
    public void delete(Long id);

    public List<Object[]> evolucionDeMatricula();
    public List<Object[]> matriculaTardiaPorPeriodo();

    public long contarCruces(LocalDate inicio, LocalDate fin, Long excluir);

    public long contarActivos(Long excluir);
}
