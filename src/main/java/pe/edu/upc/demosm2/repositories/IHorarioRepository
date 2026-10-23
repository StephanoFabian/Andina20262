package pe.edu.upc.demosm2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.upc.demosm2.entities.Horario;

import java.util.List;

public interface IHorarioRepository extends JpaRepository<Horario,Long> {
    @Query("SELECT h FROM Horario h WHERE h.asignacion.persona.idPersona = :idPersona ORDER BY h.diaSemana, h.horaInicio")
    List<Horario> findHorarioByPersonaId( Long idPersona);
}
