package pe.edu.upc.demosm2.serviceinterfaces;
import pe.edu.upc.demosm2.dtos.SesionVirtualDTO;
public interface ISesionVirtualService {
    SesionVirtualDTO buscar(Long id);
    SesionVirtualDTO guardar(Long id, SesionVirtualDTO dto);
    void eliminar(Long id);
}
