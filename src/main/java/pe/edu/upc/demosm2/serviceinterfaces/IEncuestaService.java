package pe.edu.upc.demosm2.serviceinterfaces;

import java.util.List;
import java.util.Optional;
import pe.edu.upc.demosm2.entities.Encuesta;

public interface IEncuestaService {
    public List<Encuesta> list();

    public Encuesta insert(Encuesta x);

    public Optional<Encuesta> listId(Long id);

    public void update(Encuesta x);

    public void delete(Long id);

    public List<Object[]> reporteParticipacion();
}
