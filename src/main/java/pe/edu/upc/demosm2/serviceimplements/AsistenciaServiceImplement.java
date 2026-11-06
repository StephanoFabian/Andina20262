package pe.edu.upc.demosm2.serviceimplements;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.dtos.AsistenciaDTO;
import pe.edu.upc.demosm2.repositories.IAsistenciaRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IAsistenciaService;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
@Service
@Transactional
public class AsistenciaServiceImplement implements IAsistenciaService {
    private final IAsistenciaRepository repo;
    private final EntityManager em;
    public AsistenciaServiceImplement(IAsistenciaRepository repo, EntityManager em) { this.repo=repo; this.em=em; }
    @Transactional(readOnly=true)
    public AsistenciaDTO buscar(Long id) { return convertir(existente(id)); }
    private Asistencia existente(Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe Asistencia: " + id)); }
    public AsistenciaDTO guardar(Long id, AsistenciaDTO dto) {
        Asistencia entidad = id == null ? new Asistencia() : existente(id);
        SesionVirtual sesionVirtual = em.find(SesionVirtual.class, dto.getIdSesionVirtual());
        if (sesionVirtual == null) throw new ResourceNotFoundException("No existe sesionVirtual: " + dto.getIdSesionVirtual());
        entidad.setSesionVirtual(sesionVirtual);
        Persona persona = em.find(Persona.class, dto.getIdPersona());
        if (persona == null) throw new ResourceNotFoundException("No existe persona: " + dto.getIdPersona());
        entidad.setPersona(persona);
        entidad.setEstado(dto.getEstado());
        entidad.setObservacion(dto.getObservacion());
        return convertir(repo.saveAndFlush(entidad));
    }
    public void eliminar(Long id) { repo.delete(existente(id)); repo.flush(); }
    private AsistenciaDTO convertir(Asistencia e) {
        AsistenciaDTO dto = new AsistenciaDTO();
        dto.setIdAsistencia(e.getIdAsistencia());
        dto.setEstado(e.getEstado());
        dto.setObservacion(e.getObservacion());
        dto.setIdSesionVirtual(e.getSesionVirtual().getIdSesionVirtual());
        dto.setIdPersona(e.getPersona().getIdPersona());
        return dto;
    }
}
