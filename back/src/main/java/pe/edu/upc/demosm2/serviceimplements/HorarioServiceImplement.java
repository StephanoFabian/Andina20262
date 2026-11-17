package pe.edu.upc.demosm2.serviceimplements;
import pe.edu.upc.demosm2.serviceinterfaces.IAsignacionDocenteService;

import org.springframework.stereotype.Service;

import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.dtos.HorarioDTO;
import pe.edu.upc.demosm2.repositories.IHorarioRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IHorarioService;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
@Service
public class HorarioServiceImplement implements IHorarioService {
    private final IHorarioRepository repo;
    private final IAsignacionDocenteService asignacionService;
    public HorarioServiceImplement(IHorarioRepository repo, IAsignacionDocenteService asignacionService) { this.repo=repo; this.asignacionService=asignacionService; }
        public HorarioDTO buscar(Long id) { return convertir(existente(id)); }
    private Horario existente(Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe Horario: " + id)); }
    public HorarioDTO guardar(Long id, HorarioDTO dto) {
        Horario entidad = id == null ? new Horario() : existente(id);
        AsignacionDocente asignacion = asignacionService.listId(dto.getIdAsignacion()).orElseThrow(() -> new ResourceNotFoundException("No existe asignacion"));
        if (asignacion == null) throw new ResourceNotFoundException("No existe asignacion: " + dto.getIdAsignacion());
        entidad.setAsignacion(asignacion);
        if (!dto.getHoraFin().isAfter(dto.getHoraInicio())) throw new IllegalArgumentException("La hora final debe ser posterior a la inicial");
        Long conflictos=repo.contarCruces(dto.getIdAsignacion(),dto.getDiaSemana(),dto.getHoraInicio(),dto.getHoraFin(),id);
        if (conflictos > 0) throw new IllegalArgumentException("El horario se cruza con otro de esta asignación");
        entidad.setDiaSemana(dto.getDiaSemana());
        entidad.setHoraInicio(dto.getHoraInicio());
        entidad.setHoraFin(dto.getHoraFin());
        return convertir(repo.saveAndFlush(entidad));
    }
    public void eliminar(Long id) { repo.delete(existente(id)); repo.flush(); }
    private HorarioDTO convertir(Horario e) {
        HorarioDTO dto = new HorarioDTO();
        dto.setIdHorario(e.getIdHorario());
        dto.setDiaSemana(e.getDiaSemana());
        dto.setHoraInicio(e.getHoraInicio());
        dto.setHoraFin(e.getHoraFin());
        dto.setIdAsignacion(e.getAsignacion().getId_asignacion());
        return dto;
    }
    public java.util.List<Object[]> pagina(String texto,int limite,int desplazamiento) {return repo.pagina(texto,limite,desplazamiento);}
    public long contarPagina(String texto) {return repo.contarPagina(texto);}
    public java.util.Optional<Horario> listId(Long id) {return repo.findById(id);}
}
