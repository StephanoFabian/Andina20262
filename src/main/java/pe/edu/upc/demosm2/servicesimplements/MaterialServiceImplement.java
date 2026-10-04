package pe.edu.upc.demosm2.servicesimplements;

import pe.edu.upc.demosm2.entities.Material;
import pe.edu.upc.demosm2.repositories.IMaterialRepository;
import pe.edu.upc.demosm2.servicesinterfaces.IMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.demosm2.dtos.MaterialPorTipoQuery;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialServiceImplement implements IMaterialService {

    @Autowired
    private IMaterialRepository mR;

    @Override
    public List<Material> list() {
        return mR.findAll();
    }

    @Override
    public Material insert(Material m) {
        return mR.save(m);
    }

    @Override
    public Optional<Material> listId(Long id) {
        return mR.findById(id);
    }

    @Override
    public void update(Material m) {
        mR.save(m);
    }

    @Override
    public void delete(Long id) {
        mR.deleteById(id);
    }

    @Override
    public List<Material> buscarPorTitulo(String titulo) {
        return mR.buscarPorTitulo(titulo);
    }

    @Override
    public List<Material> listarMaterialesPorCurso(Long idCurso) {
        return mR.listarMaterialesPorCurso(idCurso);
    }

    @Override
    public List<MaterialPorTipoQuery> reporteMaterialesPorTipo() {
        return mR.reporteMaterialesPorTipo();
    }
}
