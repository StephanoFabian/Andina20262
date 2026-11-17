package pe.edu.upc.demosm2.serviceimplements;

import pe.edu.upc.demosm2.entities.Material;
import pe.edu.upc.demosm2.repositories.IMaterialRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<Object[]> reporteMaterialesPorTipo() {
        return mR.reporteMaterialesPorTipo();
    }
    public java.util.List<Object[]> pagina(String texto,int limite,int desplazamiento) {return mR.pagina(texto,limite,desplazamiento);}
    public long contarPagina(String texto) {return mR.contarPagina(texto);}
    public java.util.List<String> tiposSugeridos(String texto) {return mR.tiposSugeridos(texto);}
    public java.util.List<Object[]> consultaPagina(String texto,Long idCurso,int limite,int desplazamiento) {return mR.consultaPagina(texto,idCurso,limite,desplazamiento);}
    public long contarConsulta(String texto,Long idCurso) {return mR.contarConsulta(texto,idCurso);}
    public java.util.List<Object[]> tiposPagina(int limite,int desplazamiento) {return mR.tiposPagina(limite,desplazamiento);}
    public long contarTipos() {return mR.contarTipos();}
}
