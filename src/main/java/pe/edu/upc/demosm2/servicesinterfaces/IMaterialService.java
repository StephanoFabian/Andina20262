package pe.edu.upc.demosm2.servicesinterfaces;

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
}
