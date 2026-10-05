package pe.edu.upc.demosm2.servicesimplements;

import pe.edu.upc.demosm2.entities.MaterialCurso;
import pe.edu.upc.demosm2.repositories.IMaterialCursoRepository;
import pe.edu.upc.demosm2.servicesinterfaces.IMaterialCursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.demosm2.entities.MaterialCursoId;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialCursoServiceImplement implements IMaterialCursoService {

    @Autowired
    private IMaterialCursoRepository mcR;

    @Override
    public List<MaterialCurso> list() {
        return mcR.findAll();
    }

    @Override
    public MaterialCurso insert(MaterialCurso mc) {
        return mcR.save(mc);
    }

    @Override
    public Optional<MaterialCurso> listId(MaterialCursoId id) {
        return mcR.findById(id);
    }

    @Override
    public void update(MaterialCurso mc) {
        mcR.save(mc);
    }

    @Override
    public void delete(MaterialCursoId id) {
        mcR.deleteById(id);
    }

    @Override
    public List<Object[]> reporteMaterialesPorCurso() {
        return mcR.reporteMaterialesPorCurso();
    }
}
