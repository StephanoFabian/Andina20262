package pe.edu.upc.demosm2.serviceinterfaces;

import pe.edu.upc.demosm2.entities.Rol;

import java.util.List;
import java.util.Optional;

public interface IRolService {
    public List<Rol> list();
    public Rol insert(Rol r);
    public Optional<Rol> listId(Long id);
    public void update(Rol r);
    public void delete(Long id);

    public List<Object[]> reporteCantidadPersonasPorRol();
}
