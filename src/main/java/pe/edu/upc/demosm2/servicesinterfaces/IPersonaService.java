package pe.edu.upc.demosm2.servicesinterfaces;

import pe.edu.upc.demosm2.entities.Persona;

import java.util.List;
import java.util.Optional;

public interface IPersonaService {
    public List<Persona> list();
    public Persona insert(Persona p);
    public Optional<Persona> listId(Long id);
    public void update(Persona p);
    public void delete(Long id);
}
