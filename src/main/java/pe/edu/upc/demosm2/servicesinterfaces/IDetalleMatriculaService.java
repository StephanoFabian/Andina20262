package pe.edu.upc.demosm2.servicesinterfaces;

import pe.edu.upc.demosm2.entities.DetalleMatricula;

import java.util.List;
import java.util.Optional;

public interface IDetalleMatriculaService {
    public List<DetalleMatricula> list();
    public DetalleMatricula insert(DetalleMatricula dm);
    public Optional<DetalleMatricula> listId(Long id);
    public void update(DetalleMatricula dm);
    public void delete(Long id);

    public List<Object[]> reporteRetiroPorCurso();
    public List<Object[]> reporteAulasNecesariasPorGrado(Long idPeriodo);
    public List<Object[]> reporteRetencionPorColegio(Long idPeriodoAnterior, Long idPeriodoActual);
}
