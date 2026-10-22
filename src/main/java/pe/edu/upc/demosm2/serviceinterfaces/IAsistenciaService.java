package pe.edu.upc.demosm2.serviceinterfaces;

import java.util.List;
import java.util.Optional;
import pe.edu.upc.demosm2.entities.Asistencia;

public interface IAsistenciaService {
    public List<Asistencia> list();

    public Asistencia insert(Asistencia x);

    public Optional<Asistencia> listId(Long id);

    public void update(Asistencia x);

    public void delete(Long id);

    public List<Asistencia> listarPorPersona(Long idPersona);

    public List<Object[]> reporteParticipacion(Long idPeriodo);
}
