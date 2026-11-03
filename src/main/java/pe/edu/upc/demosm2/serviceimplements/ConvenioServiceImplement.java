package pe.edu.upc.demosm2.serviceimplements;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.demosm2.entities.Convenio;
import pe.edu.upc.demosm2.repositories.IColegioRepository;
import pe.edu.upc.demosm2.repositories.IConvenioRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IConvenioService;

@Service
public class ConvenioServiceImplement implements IConvenioService {

    @Autowired
    private IConvenioRepository cR;

    @Autowired
    private IColegioRepository coR;

    @Override
    public List<Convenio> list() {
        return cR.findAll();
    }

    @Override
    public Convenio insert(Convenio x) {
        return cR.save(x);
    }

    @Override
    public Optional<Convenio> listId(Long id) {
        return cR.findById(id);
    }

    @Override
    public void update(Convenio x) {
        cR.save(x);
    }

    @Override
    public void delete(Long id) {
        cR.deleteById(id);
    }

    @Override
    public List<Object[]> reportePorVencer(Integer dias) {
        return cR.reportePorVencer(dias);
    }

    @Override
    public List<Convenio> listarVigentes() {
        return cR.listarVigentes();
    }

    @Override
    public List<Object[]> colegiosPorConvenio(String region, String tipoEntidad) {
        return cR.colegiosPorConvenio(region, tipoEntidad);
    }

    @Override
    public long cantidadColegios(Long idConvenio) {
        return coR.countByConvenio_IdConvenio(idConvenio);
    }
}
