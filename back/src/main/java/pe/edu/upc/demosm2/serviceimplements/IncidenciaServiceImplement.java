package pe.edu.upc.demosm2.serviceimplements;
import pe.edu.upc.demosm2.serviceinterfaces.ColegioServiceInterface;
import pe.edu.upc.demosm2.serviceinterfaces.IPersonaService;

import org.springframework.stereotype.Service;

import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.dtos.IncidenciaDTO;
import pe.edu.upc.demosm2.repositories.IIncidenciaRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IIncidenciaService;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
@Service
public class IncidenciaServiceImplement implements IIncidenciaService {
    private final IIncidenciaRepository repo;
    private final IPersonaService personaService;
    private final ColegioServiceInterface colegioService;
    public IncidenciaServiceImplement(IIncidenciaRepository repo, IPersonaService personaService, ColegioServiceInterface colegioService) { this.repo=repo; this.personaService=personaService; this.colegioService=colegioService; }
        public IncidenciaDTO buscar(Long id) { return convertir(existente(id)); }
    private Incidencia existente(Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe Incidencia: " + id)); }
    public IncidenciaDTO guardar(Long id, IncidenciaDTO dto) {
        Incidencia entidad = id == null ? new Incidencia() : existente(id);
        Persona persona = personaService.listId(dto.getIdPersona()).orElseThrow(() -> new ResourceNotFoundException("No existe persona"));
        if (persona == null) throw new ResourceNotFoundException("No existe persona: " + dto.getIdPersona());
        entidad.setPersona(persona);
        Colegio colegio = colegioService.listId(dto.getIdColegio()).orElseThrow(() -> new ResourceNotFoundException("No existe colegio"));
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
    public java.util.List<Object[]> pagina(String texto,int limite,int desplazamiento) {return repo.pagina(texto,limite,desplazamiento);}
    public long contarPagina(String texto) {return repo.contarPagina(texto);}
    public java.util.Optional<Incidencia> listId(Long id) {return repo.findById(id);}
}
