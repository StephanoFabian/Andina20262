package pe.edu.upc.demosm2.servicesinterfaces;

import pe.edu.upc.demosm2.dtos.RolCantidadQuery;
import pe.edu.upc.demosm2.entities.Rol;

import java.util.List;
import java.util.Optional;

public interface IRolService {
    public List<Rol> list();
    public Rol insert(Rol r);
    public Optional<Rol> listId(Long id);
    public void update(Rol r);
    public void delete(Long id);

    public List<RolCantidadQuery> reporteCantidadPersonasPorRol();
}
