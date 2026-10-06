package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.entities.CapacitacionDocente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICapacitacionDocenteRepository extends JpaRepository<CapacitacionDocente, Long> {

    // Decisión (HU13): en qué escuelas y regiones reforzar la capacitación digital de los docentes.
    // Por colegio (región = departamento): docentes evaluados, promedio y % de aprobación.
    // El colegio del docente sale de sus asignaciones. Tablas: capacitacion_docente + asignacion_docente + colegios.
    // Las escuelas menos preparadas salen primero.
    @Query(value = """
            SELECT c.departamento AS region, c.nombre AS colegio,
                   COUNT(DISTINCT cd.id_persona) AS docentesEvaluados,
                   COUNT(cd.id_capacitacion) AS evaluaciones,
                   ROUND(CAST(AVG(cd.puntaje) AS NUMERIC), 2) AS promedioPuntaje,
                   SUM(CASE WHEN UPPER(cd.estado) = 'APROBADO' THEN 1 ELSE 0 END) AS aprobadas,
                   ROUND(100.0 * SUM(CASE WHEN UPPER(cd.estado) = 'APROBADO' THEN 1 ELSE 0 END)
                         / NULLIF(SUM(CASE WHEN cd.puntaje IS NOT NULL THEN 1 ELSE 0 END), 0), 2) AS porcentajeAprobacion
            FROM capacitacion_docente cd
            INNER JOIN (SELECT DISTINCT id_persona, id_colegio FROM asignacion_docente) a ON a.id_persona = cd.id_persona
            INNER JOIN colegios c ON c.id_colegio = a.id_colegio
            GROUP BY c.departamento, c.id_colegio, c.nombre
            ORDER BY porcentajeAprobacion ASC NULLS FIRST, promedioPuntaje ASC NULLS FIRST, c.nombre
            """, nativeQuery = true)
    List<Object[]> reportePreparacionPorColegio();
}
