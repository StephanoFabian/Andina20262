package pe.edu.upc.demosm2.serviceinterfaces;
import pe.edu.upc.demosm2.dtos.AsistenciaDTO;
public interface IAsistenciaService {
    AsistenciaDTO buscar(Long id);
    AsistenciaDTO guardar(Long id, AsistenciaDTO dto);
    void eliminar(Long id);
    java.util.List<Object[]> pagina(String texto,int limite,int desplazamiento);
    long contarPagina(String texto);
    java.util.Optional<pe.edu.upc.demosm2.entities.Asistencia> listId(Long id);
}
