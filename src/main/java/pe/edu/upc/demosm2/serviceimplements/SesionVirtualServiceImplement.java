package pe.edu.upc.demosm2.serviceimplements;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.dtos.SesionVirtualDTO;
import pe.edu.upc.demosm2.repositories.ISesionVirtualRepository;
import pe.edu.upc.demosm2.serviceinterfaces.ISesionVirtualService;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
@Service
@Transactional
public class SesionVirtualServiceImplement implements ISesionVirtualService {
    private final ISesionVirtualRepository repo;
    private final EntityManager em;
    public SesionVirtualServiceImplement(ISesionVirtualRepository repo, EntityManager em) { this.repo=repo; this.em=em; }
    @Transactional(readOnly=true)
    public SesionVirtualDTO buscar(Long id) { return convertir(existente(id)); }
    private SesionVirtual existente(Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe SesionVirtual: " + id)); }
    public SesionVirtualDTO guardar(Long id, SesionVirtualDTO dto) {
        SesionVirtual entidad = id == null ? new SesionVirtual() : existente(id);
        AsignacionDocente asignacion = em.find(AsignacionDocente.class, dto.getIdAsignacion());
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
}
