package pe.edu.upc.demosm2.serviceinterfaces;


import pe.edu.upc.demosm2.entities.Horario;

import java.util.List;
import java.util.Optional;

public interface IHorarioService {
    public void insert(Horario h);
    public List<Horario> list();
    public Optional<Horario> listId(Long id);
    public void update(Horario h);
    public void delete(Long id);
    public List<Horario>obtenerHorarioPorPersona(Long idPersona);
}
