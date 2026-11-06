package pe.edu.upc.demosm2.serviceinterfaces;
import pe.edu.upc.demosm2.dtos.SesionVirtualDTO;
public interface ISesionVirtualService {
    SesionVirtualDTO buscar(Long id);
    SesionVirtualDTO guardar(Long id, SesionVirtualDTO dto);
    void eliminar(Long id);
    java.util.List<Object[]> pagina(String texto,int limite,int desplazamiento);
    long contarPagina(String texto);
    java.util.Optional<pe.edu.upc.demosm2.entities.SesionVirtual> listId(Long id);
}
