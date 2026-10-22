package pe.edu.upc.demosm2.serviceinterfaces;

import java.util.List;
import java.util.Optional;
import pe.edu.upc.demosm2.entities.Incidencia;

public interface IIncidenciaService {
    public List<Incidencia> list();

    public Incidencia insert(Incidencia x);

    public Optional<Incidencia> listId(Long id);

    public void update(Incidencia x);

    public void delete(Long id);

    public List<Incidencia> listarPorPersona(Long idPersona);

    public List<Incidencia> listarCriticasPendientes();

    public List<Object[]> reportePorPrioridad();
}
