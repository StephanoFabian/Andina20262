package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.dtos.AulasPorGradoQuery;
import pe.edu.upc.demosm2.dtos.RetencionColegioQuery;
import pe.edu.upc.demosm2.dtos.RetiroPorCursoQuery;
import pe.edu.upc.demosm2.entities.DetalleMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDetalleMatriculaRepository extends JpaRepository<DetalleMatricula, Long> {

    // Decisión: en qué cursos reforzar con tutorías porque pierden más alumnos (retiro + traslado).
    // Tablas: curso + detalles_matricula.
    @Query(value = """
            SELECT cu.nombre_curso AS curso, cu.area AS area, COUNT(d.id_d_matricula) AS totalMatriculados,
                   SUM(CASE WHEN UPPER(d.estado) = 'RETIRADO' THEN 1 ELSE 0 END) AS retirados,
                   SUM(CASE WHEN UPPER(d.estado) = 'TRASLADADO' THEN 1 ELSE 0 END) AS trasladados,
                   ROUND(100.0 * SUM(CASE WHEN UPPER(d.estado) IN ('RETIRADO', 'TRASLADADO') THEN 1 ELSE 0 END)
                         / COUNT(d.id_d_matricula), 2) AS porcentajePerdida
            FROM curso cu
            INNER JOIN detalles_matricula d ON d.id_curso = cu.id_curso
            GROUP BY cu.id_curso, cu.nombre_curso, cu.area
            ORDER BY porcentajePerdida DESC, totalMatriculados DESC
            """, nativeQuery = true)
    List<RetiroPorCursoQuery> reporteRetiroPorCurso();

    // Decisión: cuántas secciones abrir por grado en un periodo (máximo 40 alumnos por aula).
    // Tablas: grados + detalles_matricula.
    @Query(value = """
            SELECT g.nombre AS grado, g.nivel AS nivel, COUNT(d.id_d_matricula) AS vigentes,
                   CAST(CEIL(COUNT(d.id_d_matricula) / 40.0) AS BIGINT) AS aulasNecesarias
            FROM grados g
            INNER JOIN detalles_matricula d ON d.id_grado = g.id_grado
            WHERE d.id_periodo = :idPeriodo AND UPPER(d.estado) = 'VIGENTE'
            GROUP BY g.id_grado, g.nombre, g.nivel
            ORDER BY vigentes DESC
            """, nativeQuery = true)
    List<AulasPorGradoQuery> reporteAulasNecesariasPorGrado(@Param("idPeriodo") Long idPeriodo);

    // Decisión: cuánta población gana o pierde cada colegio de un periodo a otro.
    // Por colegio: alumnos del periodo anterior y del actual, cuántos siguieron, cuántos se fueron, cuántos son nuevos.
    // Un alumno "está" en un periodo si tiene matrícula en él y no figura como RETIRADO ni TRASLADADO.
    // Tablas: detalles_matricula + matriculas + colegios. Los que más pierden salen primero.
    @Query(value = """
            SELECT c.nombre AS colegio,
                   SUM(x.anterior) AS alumnosAnterior,
                   SUM(x.actual) AS alumnosActual,
                   SUM(CASE WHEN x.anterior = 1 AND x.actual = 1 THEN 1 ELSE 0 END) AS siguieron,
                   SUM(CASE WHEN x.anterior = 1 AND x.actual = 0 THEN 1 ELSE 0 END) AS seFueron,
                   SUM(CASE WHEN x.anterior = 0 AND x.actual = 1 THEN 1 ELSE 0 END) AS nuevos,
                   SUM(x.actual) - SUM(x.anterior) AS variacion
            FROM (
                SELECT m.id_colegio, m.id_persona,
                       MAX(CASE WHEN d.id_periodo = :idPeriodoAnterior
                                 AND UPPER(d.estado) NOT IN ('RETIRADO', 'TRASLADADO') THEN 1 ELSE 0 END) AS anterior,
                       MAX(CASE WHEN d.id_periodo = :idPeriodoActual
                                 AND UPPER(d.estado) NOT IN ('RETIRADO', 'TRASLADADO') THEN 1 ELSE 0 END) AS actual
                FROM detalles_matricula d
                INNER JOIN matriculas m ON m.id_matricula = d.id_matricula
                WHERE d.id_periodo IN (:idPeriodoAnterior, :idPeriodoActual)
                GROUP BY m.id_colegio, m.id_persona
            ) x
            INNER JOIN colegios c ON c.id_colegio = x.id_colegio
            GROUP BY c.id_colegio, c.nombre
            ORDER BY variacion ASC, c.nombre
            """, nativeQuery = true)
    List<RetencionColegioQuery> reporteRetencionPorColegio(@Param("idPeriodoAnterior") Long idPeriodoAnterior,
                                                           @Param("idPeriodoActual") Long idPeriodoActual);
}
