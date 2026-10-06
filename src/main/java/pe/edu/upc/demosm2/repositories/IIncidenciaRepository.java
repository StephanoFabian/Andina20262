package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.entities.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IIncidenciaRepository extends JpaRepository<Incidencia, Long> {

    // HU14: incidencias que reportó una persona, la más reciente primero
    @Query(value = "SELECT * FROM incidencia WHERE id_persona = :idPersona ORDER BY fecha_reporte DESC", nativeQuery = true)
    List<Incidencia> listarPorPersona(@Param("idPersona") Long idPersona);

    // HU14: alertas para el equipo técnico: críticas sin resolver; primero las que afectan una sesión en curso
    @Query(value = """
            SELECT * FROM incidencia
            WHERE UPPER(prioridad) = 'CRITICA' AND UPPER(estado) IN ('ABIERTA', 'EN_PROCESO')
            ORDER BY afecta_sesion DESC, fecha_reporte
            """, nativeQuery = true)
    List<Incidencia> listarCriticasPendientes();

    // Decisión (HU14): dónde poner más soporte: prioridades con más pendientes y más horas de resolución.
    @Query(value = """
            SELECT UPPER(prioridad) AS prioridad, COUNT(*) AS total,
                   SUM(CASE WHEN UPPER(estado) IN ('ABIERTA', 'EN_PROCESO') THEN 1 ELSE 0 END) AS pendientes,
                   SUM(CASE WHEN UPPER(estado) IN ('RESUELTA', 'CERRADA') THEN 1 ELSE 0 END) AS resueltas,
                   ROUND(CAST(AVG(EXTRACT(EPOCH FROM (fecha_cierre - fecha_reporte)) / 3600.0) AS NUMERIC), 2) AS horasPromedioResolucion
            FROM incidencia
            GROUP BY UPPER(prioridad)
            ORDER BY CASE UPPER(prioridad) WHEN 'CRITICA' THEN 1 WHEN 'ALTA' THEN 2 WHEN 'MEDIA' THEN 3 ELSE 4 END
            """, nativeQuery = true)
    List<Object[]> reportePorPrioridad();
}
