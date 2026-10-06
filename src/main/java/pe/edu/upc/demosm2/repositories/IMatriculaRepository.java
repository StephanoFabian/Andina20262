package pe.edu.upc.demosm2.repositories;

import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.upc.demosm2.entities.Matricula;

import java.util.List;

@Repository
public interface IMatriculaRepository extends JpaRepository<Matricula, Long> {

    // Decisión: dónde concentrar docentes, materiales y aulas (y qué colegios no tienen alumnos).
    // Tablas: colegios + matriculas.
    @Query(value = "SELECT c.nombre, c.departamento, c.tipo_zona, COUNT(m.id_matricula) AS total_matriculas, " +
            " ROUND(100.0 * COUNT(m.id_matricula) / NULLIF(SUM(COUNT(m.id_matricula)) OVER (), 0), 2) AS porcentaje_del_total " +
            " FROM colegios c LEFT JOIN matriculas m ON m.id_colegio = c.id_colegio " +
            " GROUP BY c.id_colegio, c.nombre, c.departamento, c.tipo_zona " +
            " ORDER BY total_matriculas DESC, c.nombre", nativeQuery = true)
    List<Object[]> matriculasPorColegio();

    // Decisión: depurar el padrón (matrículas duplicadas o alumnos que se cambiaron de colegio).
    // Tablas: persona + matriculas.
    @Query(value = "SELECT p.id_persona, p.nombres_persona, p.apellidos_persona, " +
            " COUNT(m.id_matricula) AS matriculas, COUNT(DISTINCT m.id_colegio) AS colegios " +
            " FROM persona p INNER JOIN matriculas m ON m.id_persona = p.id_persona " +
            " GROUP BY p.id_persona, p.nombres_persona, p.apellidos_persona " +
            " HAVING COUNT(m.id_matricula) > 1 " +
            " ORDER BY colegios DESC, matriculas DESC", nativeQuery = true)
    List<Object[]> estudiantesConVariasMatriculas();

    // HU34/HU42: matrícula repetida del mismo estudiante en el mismo colegio (sin contar la que se edita).
    @Query(value = "SELECT COUNT(*) FROM matriculas WHERE id_persona = :idPersona AND id_colegio = :idColegio AND id_matricula <> :excluir",
            nativeQuery = true)
    long contarDuplicadas(@Param("idPersona") Long idPersona, @Param("idColegio") Long idColegio, @Param("excluir") Long excluir);
}
