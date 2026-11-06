package pe.edu.upc.demosm2.serviceinterfaces;
import pe.edu.upc.demosm2.dtos.AsistenciaDTO;
public interface IAsistenciaService {
    AsistenciaDTO buscar(Long id);
    AsistenciaDTO guardar(Long id, AsistenciaDTO dto);
    void eliminar(Long id);
}
