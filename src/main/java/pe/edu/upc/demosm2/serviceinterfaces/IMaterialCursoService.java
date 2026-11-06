package pe.edu.upc.demosm2.serviceinterfaces;

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
    java.util.List<Object[]> pagina(String texto,int limite,int desplazamiento, Long idMaterial);
    long contarPagina(String texto, Long idMaterial);
    java.util.List<Object[]> cursosPagina(int limite,int desplazamiento);
    long contarCursos();
}
