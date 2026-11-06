package pe.edu.upc.demosm2.serviceinterfaces;
import pe.edu.upc.demosm2.dtos.IncidenciaDTO;
public interface IIncidenciaService {
    IncidenciaDTO buscar(Long id);
    IncidenciaDTO guardar(Long id, IncidenciaDTO dto);
    void eliminar(Long id);
    java.util.List<Object[]> pagina(String texto,int limite,int desplazamiento);
    long contarPagina(String texto);
    java.util.Optional<pe.edu.upc.demosm2.entities.Incidencia> listId(Long id);
}
