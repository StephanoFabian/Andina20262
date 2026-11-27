package pe.edu.upc.demosm2.serviceinterfaces;

import pe.edu.upc.demosm2.entities.Material;

import java.util.List;
import java.util.Optional;

public interface IMaterialService {
    public List<Material> list();
    public Material insert(Material m);
    public Optional<Material> listId(Long id);
    public void update(Material m);
    public void delete(Long id);

    public List<Material> buscarPorTitulo(String titulo);
    public List<Material> listarMaterialesPorCurso(Long idCurso);
    public List<Object[]> reporteMaterialesPorTipo();
    java.util.List<Object[]> pagina(String texto,int limite,int desplazamiento);
    long contarPagina(String texto);
    java.util.List<String> tiposSugeridos(String texto);
    java.util.List<Object[]> consultaPagina(String texto,Long idCurso,int limite,int desplazamiento);
    long contarConsulta(String texto,Long idCurso);
    java.util.List<Object[]> tiposPagina(int limite,int desplazamiento);
    long contarTipos();
}
