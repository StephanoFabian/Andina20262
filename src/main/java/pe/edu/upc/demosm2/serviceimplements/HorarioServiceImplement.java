package pe.edu.upc.demosm2.serviceimplements;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.dtos.HorarioDTO;
import pe.edu.upc.demosm2.repositories.IHorarioRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IHorarioService;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
@Service
@Transactional
public class HorarioServiceImplement implements IHorarioService {
    private final IHorarioRepository repo;
    private final EntityManager em;
    public HorarioServiceImplement(IHorarioRepository repo, EntityManager em) { this.repo=repo; this.em=em; }
    @Transactional(readOnly=true)
    public HorarioDTO buscar(Long id) { return convertir(existente(id)); }
    private Horario existente(Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe Horario: " + id)); }
    public HorarioDTO guardar(Long id, HorarioDTO dto) {
        Horario entidad = id == null ? new Horario() : existente(id);
        AsignacionDocente asignacion = em.find(AsignacionDocente.class, dto.getIdAsignacion(), LockModeType.PESSIMISTIC_WRITE);
        if (asignacion == null) throw new ResourceNotFoundException("No existe asignacion: " + dto.getIdAsignacion());
        entidad.setAsignacion(asignacion);
        if (!dto.getHoraFin().isAfter(dto.getHoraInicio())) throw new IllegalArgumentException("La hora final debe ser posterior a la inicial");
        Long conflictos = em.createQuery("select count(h) from Horario h where h.asignacion.id_asignacion = :asignacion and h.diaSemana = :dia and h.horaInicio < :fin and h.horaFin > :inicio and (:id is null or h.idHorario <> :id)", Long.class)
                .setParameter("asignacion", dto.getIdAsignacion()).setParameter("dia", dto.getDiaSemana())
                .setParameter("inicio", dto.getHoraInicio()).setParameter("fin", dto.getHoraFin()).setParameter("id", id).getSingleResult();
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
}
