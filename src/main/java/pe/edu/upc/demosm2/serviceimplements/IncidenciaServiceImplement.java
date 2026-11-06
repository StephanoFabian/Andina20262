package pe.edu.upc.demosm2.serviceimplements;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.dtos.IncidenciaDTO;
import pe.edu.upc.demosm2.repositories.IIncidenciaRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IIncidenciaService;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
@Service
@Transactional
public class IncidenciaServiceImplement implements IIncidenciaService {
    private final IIncidenciaRepository repo;
    private final EntityManager em;
    public IncidenciaServiceImplement(IIncidenciaRepository repo, EntityManager em) { this.repo=repo; this.em=em; }
    @Transactional(readOnly=true)
    public IncidenciaDTO buscar(Long id) { return convertir(existente(id)); }
    private Incidencia existente(Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe Incidencia: " + id)); }
    public IncidenciaDTO guardar(Long id, IncidenciaDTO dto) {
        Incidencia entidad = id == null ? new Incidencia() : existente(id);
        Persona persona = em.find(Persona.class, dto.getIdPersona());
        if (persona == null) throw new ResourceNotFoundException("No existe persona: " + dto.getIdPersona());
        entidad.setPersona(persona);
        Colegio colegio = em.find(Colegio.class, dto.getIdColegio());
        if (colegio == null) throw new ResourceNotFoundException("No existe colegio: " + dto.getIdColegio());
        entidad.setColegio(colegio);
        entidad.setTitulo(dto.getTitulo());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setFecha(dto.getFecha());
        entidad.setEstado(dto.getEstado());
        entidad.setPrioridad(dto.getPrioridad());
        return convertir(repo.saveAndFlush(entidad));
    }
    public void eliminar(Long id) { repo.delete(existente(id)); repo.flush(); }
    private IncidenciaDTO convertir(Incidencia e) {
        IncidenciaDTO dto = new IncidenciaDTO();
        dto.setIdIncidencia(e.getIdIncidencia());
        dto.setTitulo(e.getTitulo());
        dto.setDescripcion(e.getDescripcion());
        dto.setFecha(e.getFecha());
        dto.setEstado(e.getEstado());
        dto.setPrioridad(e.getPrioridad());
        dto.setIdPersona(e.getPersona().getIdPersona());
        dto.setIdColegio(e.getColegio().getIdColegio());
        return dto;
    }
}
