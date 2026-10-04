package pe.edu.upc.demosm2.servicesimplements;

import pe.edu.upc.demosm2.entities.PerfilAcademico;
import pe.edu.upc.demosm2.repositories.IPerfilAcademicoRepository;
import pe.edu.upc.demosm2.servicesinterfaces.IPerfilAcademicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PerfilAcademicoServiceImplement implements IPerfilAcademicoService {

    @Autowired
    private IPerfilAcademicoRepository paR;

    @Override
    public List<PerfilAcademico> list() {
        return paR.findAll();
    }

    @Override
    public PerfilAcademico insert(PerfilAcademico pa) {
        return paR.save(pa);
    }

    @Override
    public Optional<PerfilAcademico> listId(Long id) {
        return paR.findById(id);
    }

    @Override
    public void update(PerfilAcademico pa) {
        paR.save(pa);
    }

    @Override
    public void delete(Long id) {
        paR.deleteById(id);
    }
}
