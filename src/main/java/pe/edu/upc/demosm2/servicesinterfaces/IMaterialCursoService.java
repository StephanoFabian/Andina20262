package pe.edu.upc.demosm2.servicesinterfaces;

import pe.edu.upc.demosm2.entities.MaterialCurso;
import pe.edu.upc.demosm2.entities.MaterialCursoId;

import java.util.List;
import java.util.Optional;

public interface IMaterialCursoService {
    public List<MaterialCurso> list();
    public MaterialCurso insert(MaterialCurso mc);
    public Optional<MaterialCurso> listId(MaterialCursoId id);
    public void update(MaterialCurso mc);
    public void delete(MaterialCursoId id);

    public List<Object[]> reporteMaterialesPorCurso();
}
