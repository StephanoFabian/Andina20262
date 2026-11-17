package pe.edu.upc.demosm2.serviceinterfaces;
import pe.edu.upc.demosm2.dtos.ConvenioDTO;
public interface IConvenioService {
    ConvenioDTO buscar(Long id);
    ConvenioDTO guardar(Long id, ConvenioDTO dto);
    void eliminar(Long id);
    java.util.List<Object[]> pagina(String texto,int limite,int desplazamiento);
    long contarPagina(String texto);
    java.util.List<Object[]> vencimientosPagina(int dias,int limite,int desplazamiento);
    long contarVencimientos(int dias);
    java.util.Optional<pe.edu.upc.demosm2.entities.Convenio> listId(Long id);
}
