package pe.edu.upc.demosm2.serviceimplements;

import pe.edu.upc.demosm2.entities.Rol;
import pe.edu.upc.demosm2.repositories.IRolRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImplement implements IRolService {

    @Autowired
    private IRolRepository rR;

    @Override
    public List<Rol> list() {
        return rR.findAll();
    }

    @Override
    public Rol insert(Rol r) {
        return rR.save(r);
    }

    @Override
    public Optional<Rol> listId(Long id) {
        return rR.findById(id);
    }

    @Override
    public void update(Rol r) {
        rR.save(r);
    }

    @Override
    public void delete(Long id) {
        rR.deleteById(id);
    }

    @Override
    public List<Object[]> reporteCantidadPersonasPorRol() {
        return rR.reporteCantidadPersonasPorRol();
    }

    @Override
    public long contarPersonasDelRol(Long idRol) {
        return rR.contarPersonasDelRol(idRol);
    }
}
