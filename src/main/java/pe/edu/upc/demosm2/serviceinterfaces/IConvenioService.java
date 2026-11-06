package pe.edu.upc.demosm2.serviceinterfaces;
import pe.edu.upc.demosm2.dtos.ConvenioDTO;
public interface IConvenioService {
    ConvenioDTO buscar(Long id);
    ConvenioDTO guardar(Long id, ConvenioDTO dto);
    void eliminar(Long id);
}
