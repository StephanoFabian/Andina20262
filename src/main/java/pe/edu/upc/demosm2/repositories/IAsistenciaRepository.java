package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.entities.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAsistenciaRepository extends JpaRepository<Asistencia, Long> {

    // HU08: asistencias de un estudiante, la más reciente primero
    @Query(value = "SELECT * FROM asistencia WHERE id_persona = :idPersona ORDER BY fecha_sesion DESC, id_asistencia DESC",
            nativeQuery = true)
    List<Asistencia> listarPorPersona(@Param("idPersona") Long idPersona);

    // Decisión (HU08): a qué alumnos visitar o reforzar antes de que abandonen.
    // Por estudiante en un periodo: sesiones, asistencias, faltas, % de asistencia y promedio de notas.
    // Las justificadas no cuentan como falta. Tablas: persona + asistencia. Los de menor asistencia salen primero.
    @Query(value = """
            SELECT p.id_persona AS idPersona, p.nombres_persona AS nombres, p.apellidos_persona AS apellidos,
                   COUNT(a.id_asistencia) AS sesiones,
                   SUM(CASE WHEN UPPER(a.estado) IN ('PRESENTE', 'TARDANZA') THEN 1 ELSE 0 END) AS asistencias,
                   SUM(CASE WHEN UPPER(a.estado) = 'AUSENTE' THEN 1 ELSE 0 END) AS faltas,
                   SUM(CASE WHEN UPPER(a.estado) = 'JUSTIFICADO' THEN 1 ELSE 0 END) AS justificadas,
                   ROUND(100.0 * SUM(CASE WHEN UPPER(a.estado) IN ('PRESENTE', 'TARDANZA') THEN 1 ELSE 0 END)
                         / NULLIF(SUM(CASE WHEN UPPER(a.estado) <> 'JUSTIFICADO' THEN 1 ELSE 0 END), 0), 2) AS porcentajeAsistencia,
                   ROUND(CAST(AVG(a.calificacion) AS NUMERIC), 2) AS promedioCalificacion
            FROM persona p
            INNER JOIN asistencia a ON a.id_persona = p.id_persona
            WHERE a.id_periodo = :idPeriodo
            GROUP BY p.id_persona, p.nombres_persona, p.apellidos_persona
            ORDER BY porcentajeAsistencia ASC NULLS LAST, promedioCalificacion ASC NULLS LAST, p.id_persona
            """, nativeQuery = true)
    List<Object[]> reporteParticipacion(@Param("idPeriodo") Long idPeriodo);
}
