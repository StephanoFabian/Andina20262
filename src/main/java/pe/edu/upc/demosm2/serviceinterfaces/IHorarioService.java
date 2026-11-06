package pe.edu.upc.demosm2.serviceinterfaces;
import pe.edu.upc.demosm2.dtos.HorarioDTO;
public interface IHorarioService {
    HorarioDTO buscar(Long id);
    HorarioDTO guardar(Long id, HorarioDTO dto);
    void eliminar(Long id);
}
