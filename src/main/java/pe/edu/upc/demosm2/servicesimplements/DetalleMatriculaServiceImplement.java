package pe.edu.upc.demosm2.servicesimplements;

import pe.edu.upc.demosm2.entities.DetalleMatricula;
import pe.edu.upc.demosm2.repositories.IDetalleMatriculaRepository;
import pe.edu.upc.demosm2.servicesinterfaces.IDetalleMatriculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleMatriculaServiceImplement implements IDetalleMatriculaService {

    @Autowired
    private IDetalleMatriculaRepository dmR;

    @Override
    public List<DetalleMatricula> list() {
        return dmR.findAll();
    }

    @Override
    public DetalleMatricula insert(DetalleMatricula dm) {
        return dmR.save(dm);
    }

    @Override
    public Optional<DetalleMatricula> listId(Long id) {
        return dmR.findById(id);
    }

    @Override
    public void update(DetalleMatricula dm) {
        dmR.save(dm);
    }

    @Override
    public void delete(Long id) {
        dmR.deleteById(id);
    }

    @Override
    public List<DetalleMatricula> listarDetallesPorAlumno(Long idPersona) {
        return dmR.listarDetallesPorAlumno(idPersona);
    }

    @Override
    public List<Object[]> reporteRetiroPorCurso() {
        return dmR.reporteRetiroPorCurso();
    }

    @Override
    public List<Object[]> reporteAulasNecesariasPorGrado(Long idPeriodo) {
        return dmR.reporteAulasNecesariasPorGrado(idPeriodo);
    }
}
