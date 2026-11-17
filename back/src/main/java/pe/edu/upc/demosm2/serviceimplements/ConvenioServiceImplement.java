package pe.edu.upc.demosm2.serviceimplements;
import pe.edu.upc.demosm2.serviceinterfaces.ColegioServiceInterface;

import org.springframework.stereotype.Service;

import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.dtos.ConvenioDTO;
import pe.edu.upc.demosm2.repositories.IConvenioRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IConvenioService;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
@Service
public class ConvenioServiceImplement implements IConvenioService {
    private final IConvenioRepository repo;
    private final ColegioServiceInterface colegioService;
    public ConvenioServiceImplement(IConvenioRepository repo, ColegioServiceInterface colegioService) { this.repo=repo; this.colegioService=colegioService; }
        public ConvenioDTO buscar(Long id) { return convertir(existente(id)); }
    private Convenio existente(Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe Convenio: " + id)); }
    public ConvenioDTO guardar(Long id, ConvenioDTO dto) {
        Convenio entidad = id == null ? new Convenio() : existente(id);
        if (dto.getIdColegio()!=null) {
            Colegio colegio=colegioService.listId(dto.getIdColegio()).orElseThrow(() -> new ResourceNotFoundException("No existe colegio"));
            if(colegio==null) throw new ResourceNotFoundException("No existe colegio: "+dto.getIdColegio());
            entidad.setColegio(colegio);
        }
        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) throw new IllegalArgumentException("La fecha final debe ser posterior o igual a la inicial");
        entidad.setNombre(dto.getNombre());
        entidad.setInstitucion(dto.getEntidad()!=null && !dto.getEntidad().isBlank() ? dto.getEntidad().trim() : dto.getInstitucion().trim());
        if(dto.getTipoEntidad()!=null) entidad.setTipoEntidad(dto.getTipoEntidad());
        if(dto.getRegion()!=null) entidad.setRegion(dto.getRegion().trim());
        if(dto.getPresupuesto()!=null) entidad.setPresupuesto(dto.getPresupuesto());
        entidad.setFechaInicio(dto.getFechaInicio());
        entidad.setFechaFin(dto.getFechaFin());
        entidad.setEstado(dto.getEstado()==null ? "VIGENTE" : dto.getEstado());
        return convertir(repo.saveAndFlush(entidad));
    }
    public void eliminar(Long id) { repo.delete(existente(id)); repo.flush(); }
    private ConvenioDTO convertir(Convenio e) {
        ConvenioDTO dto = new ConvenioDTO();
        dto.setIdConvenio(e.getIdConvenio());
        dto.setNombre(e.getNombre());
        dto.setInstitucion(e.getInstitucion());
        dto.setFechaInicio(e.getFechaInicio());
        dto.setFechaFin(e.getFechaFin());
        dto.setEstado(e.getEstado());
        dto.setIdColegio(e.getColegio()==null ? null : e.getColegio().getIdColegio());
        dto.setEntidad(e.getInstitucion());
        dto.setTipoEntidad(e.getTipoEntidad());
        dto.setRegion(e.getRegion());
        dto.setPresupuesto(e.getPresupuesto());
        return dto;
    }
    public java.util.List<Object[]> pagina(String texto,int limite,int desplazamiento) {return repo.pagina(texto,limite,desplazamiento);}
    public long contarPagina(String texto) {return repo.contarPagina(texto);}
    public java.util.List<Object[]> vencimientosPagina(int dias,int limite,int desplazamiento) {return repo.vencimientosPagina(dias,limite,desplazamiento);}
    public long contarVencimientos(int dias) {return repo.contarVencimientos(dias);}
    public java.util.Optional<Convenio> listId(Long id) {return repo.findById(id);}
}
