package pe.edu.upc.demosm2.serviceimplements;
import pe.edu.upc.demosm2.serviceinterfaces.IPersonaService;
import pe.edu.upc.demosm2.serviceinterfaces.ISesionVirtualService;

import org.springframework.stereotype.Service;

import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.dtos.AsistenciaDTO;
import pe.edu.upc.demosm2.repositories.IAsistenciaRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IAsistenciaService;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
@Service
public class AsistenciaServiceImplement implements IAsistenciaService {
    private final IAsistenciaRepository repo;
    private final ISesionVirtualService sesionVirtualService;
    private final IPersonaService personaService;
    public AsistenciaServiceImplement(IAsistenciaRepository repo, ISesionVirtualService sesionVirtualService, IPersonaService personaService) { this.repo=repo; this.sesionVirtualService=sesionVirtualService; this.personaService=personaService; }
        public AsistenciaDTO buscar(Long id) { return convertir(existente(id)); }
    private Asistencia existente(Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe Asistencia: " + id)); }
    public AsistenciaDTO guardar(Long id, AsistenciaDTO dto) {
        Asistencia entidad = id == null ? new Asistencia() : existente(id);
        SesionVirtual sesionVirtual = sesionVirtualService.listId(dto.getIdSesionVirtual()).orElseThrow(() -> new ResourceNotFoundException("No existe sesionVirtual"));
        if (sesionVirtual == null) throw new ResourceNotFoundException("No existe sesionVirtual: " + dto.getIdSesionVirtual());
        entidad.setSesionVirtual(sesionVirtual);
        Persona persona = personaService.listId(dto.getIdPersona()).orElseThrow(() -> new ResourceNotFoundException("No existe persona"));
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
    public java.util.List<Object[]> pagina(String texto,int limite,int desplazamiento) {return repo.pagina(texto,limite,desplazamiento);}
    public long contarPagina(String texto) {return repo.contarPagina(texto);}
    public java.util.Optional<Asistencia> listId(Long id) {return repo.findById(id);}
}
