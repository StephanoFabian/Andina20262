package pe.edu.upc.demosm2.servicesinterfaces;

import pe.edu.upc.demosm2.dtos.AulasPorGradoQuery;
import pe.edu.upc.demosm2.dtos.RetencionColegioQuery;
import pe.edu.upc.demosm2.dtos.RetiroPorCursoQuery;
import pe.edu.upc.demosm2.entities.DetalleMatricula;

import java.util.List;
import java.util.Optional;

public interface IDetalleMatriculaService {
    public List<DetalleMatricula> list();
    public DetalleMatricula insert(DetalleMatricula dm);
    public Optional<DetalleMatricula> listId(Long id);
    public void update(DetalleMatricula dm);
    public void delete(Long id);

    public List<RetiroPorCursoQuery> reporteRetiroPorCurso();
    public List<AulasPorGradoQuery> reporteAulasNecesariasPorGrado(Long idPeriodo);
    public List<RetencionColegioQuery> reporteRetencionPorColegio(Long idPeriodoAnterior, Long idPeriodoActual);
}
