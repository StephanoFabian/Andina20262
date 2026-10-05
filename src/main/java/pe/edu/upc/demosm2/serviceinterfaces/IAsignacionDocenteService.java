package pe.edu.upc.demosm2.serviceinterfaces;

import pe.edu.upc.demosm2.dtos.AsignacionDTOList;
import pe.edu.upc.demosm2.entities.AsignacionDocente;
import pe.edu.upc.demosm2.entities.Curso;

import java.util.List;
import java.util.Optional;

public interface IAsignacionDocenteService {
    public void insert(AsignacionDocente d);
    public List<AsignacionDocente> list();
    public Optional<AsignacionDocente> listId(Long id);
    public void update(AsignacionDocente aD);
    public void delete(Long id);
    public List<AsignacionDocente> ObtenerPorRangoHoras(Long hora_min, Long hora_max);
}
