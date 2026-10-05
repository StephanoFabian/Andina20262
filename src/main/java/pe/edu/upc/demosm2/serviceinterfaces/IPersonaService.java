package pe.edu.upc.demosm2.serviceinterfaces;

import pe.edu.upc.demosm2.entities.Persona;

import java.util.List;
import java.util.Optional;

public interface IPersonaService {
    public List<Persona> list();
    public Persona insert(Persona p);
    public Optional<Persona> listId(Long id);
    public void update(Persona p);
    public void delete(Long id);

    public List<Object[]> reporteCuentasPorRol();

    public List<Persona> filtrarPorAulaYEstado(Long idAula, String estado);

    // Guarda la persona tal cual (no toca la contraseña); se usa para la reasignación de aula
    public Persona reasignarAula(Persona p);

    // true si la persona tiene alguno de los roles indicados (se compara el detalle del rol sin distinguir mayúsculas)
    public boolean tieneAlgunRol(Persona p, String... roles);
}
