package pe.edu.upc.demosm2.serviceimplements;

import pe.edu.upc.demosm2.entities.Persona;
import pe.edu.upc.demosm2.repositories.IPersonaRepositories;
import pe.edu.upc.demosm2.serviceinterfaces.IPersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaServiceImplement implements IPersonaService {

    @Autowired
    private IPersonaRepositories pR;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Persona> list() {
        return pR.findAll();
    }

    @Override
    public Persona insert(Persona p) {
        // Solo se guarda el hash BCrypt de la contraseña
        p.setPasswordPersona(passwordEncoder.encode(p.getPasswordPersona()));
        return pR.save(p);
    }

    @Override
    public Optional<Persona> listId(Long id) {
        return pR.findById(id);
    }

    @Override
    public void update(Persona p) {
        if (p.getPasswordPersona() == null || p.getPasswordPersona().isBlank()) {
            // Si no mandan contraseña nueva, se conserva el hash que ya tenía
            pR.findById(p.getIdPersona()).ifPresent(actual -> p.setPasswordPersona(actual.getPasswordPersona()));
        } else {
            p.setPasswordPersona(passwordEncoder.encode(p.getPasswordPersona()));
        }
        pR.save(p);
    }

    @Override
    public void delete(Long id) {
        pR.deleteById(id);
    }

    @Override
    public List<Object[]> reporteCuentasPorRol() {
        return pR.reporteCuentasPorRol();
    }

    @Override
    public List<Persona> filtrarPorAulaYEstado(Long idAula, String estado) {
        return pR.filtrarPorAulaYEstado(idAula, estado);
    }

    @Override
    public Persona reasignarAula(Persona p) {
        return pR.save(p);
    }

    @Override
    public boolean tieneAlgunRol(Persona p, String... roles) {
        if (p == null || p.getRol() == null || p.getRol().getDetalle() == null) return false;
        String rol = p.getRol().getDetalle().trim();
        for (String r : roles) {
            if (r.equalsIgnoreCase(rol)) return true;
        }
        return false;
    }
}
