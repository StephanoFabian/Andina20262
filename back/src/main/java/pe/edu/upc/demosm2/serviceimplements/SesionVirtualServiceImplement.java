package pe.edu.upc.demosm2.serviceimplements;
import pe.edu.upc.demosm2.serviceinterfaces.IAsignacionDocenteService;

import org.springframework.stereotype.Service;

import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.dtos.SesionVirtualDTO;
import pe.edu.upc.demosm2.repositories.ISesionVirtualRepository;
import pe.edu.upc.demosm2.serviceinterfaces.ISesionVirtualService;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
@Service
public class SesionVirtualServiceImplement implements ISesionVirtualService {
    private final ISesionVirtualRepository repo;
    private final IAsignacionDocenteService asignacionService;
    public SesionVirtualServiceImplement(ISesionVirtualRepository repo, IAsignacionDocenteService asignacionService) { this.repo=repo; this.asignacionService=asignacionService; }
        public SesionVirtualDTO buscar(Long id) { return convertir(existente(id)); }
    private SesionVirtual existente(Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe SesionVirtual: " + id)); }
    public SesionVirtualDTO guardar(Long id, SesionVirtualDTO dto) {
        SesionVirtual entidad = id == null ? new SesionVirtual() : existente(id);
        AsignacionDocente asignacion = asignacionService.listId(dto.getIdAsignacion()).orElseThrow(() -> new ResourceNotFoundException("No existe asignacion"));
        if (asignacion == null) throw new ResourceNotFoundException("No existe asignacion: " + dto.getIdAsignacion());
        entidad.setAsignacion(asignacion);
        if (!dto.getHoraFin().isAfter(dto.getHoraInicio())) throw new IllegalArgumentException("La hora final debe ser posterior a la inicial");
        entidad.setTitulo(dto.getTitulo());
        entidad.setFecha(dto.getFecha());
        entidad.setHoraInicio(dto.getHoraInicio());
        entidad.setHoraFin(dto.getHoraFin());
        entidad.setEnlace(dto.getEnlace());
        entidad.setEstado(dto.getEstado());
        return convertir(repo.saveAndFlush(entidad));
    }
    public void eliminar(Long id) { repo.delete(existente(id)); repo.flush(); }
    private SesionVirtualDTO convertir(SesionVirtual e) {
        SesionVirtualDTO dto = new SesionVirtualDTO();
        dto.setIdSesionVirtual(e.getIdSesionVirtual());
        dto.setTitulo(e.getTitulo());
        dto.setFecha(e.getFecha());
        dto.setHoraInicio(e.getHoraInicio());
        dto.setHoraFin(e.getHoraFin());
        dto.setEnlace(e.getEnlace());
        dto.setEstado(e.getEstado());
        dto.setIdAsignacion(e.getAsignacion().getId_asignacion());
        return dto;
    }
    public java.util.List<Object[]> pagina(String texto,int limite,int desplazamiento) {return repo.pagina(texto,limite,desplazamiento);}
    public long contarPagina(String texto) {return repo.contarPagina(texto);}
    public java.util.Optional<SesionVirtual> listId(Long id) {return repo.findById(id);}
}
