package pe.edu.upc.demosm2.repositories;

import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.upc.demosm2.entities.PeriodoAcademico;

import java.util.List;

@Repository
public interface IPeriodoAcademicoRepository extends JpaRepository<PeriodoAcademico, Long> {

    // Decisión: proyectar vacantes y detectar caídas de matrícula entre un periodo y el anterior.
    // Tablas: periodos_academicos + detalles_matricula.
    @Query(value = "SELECT p.nombre, p.fecha_inicio, p.estado, COUNT(d.id_d_matricula) AS matriculados, " +
            " LAG(COUNT(d.id_d_matricula)) OVER (ORDER BY p.fecha_inicio) AS periodo_anterior, " +
            " ROUND(100.0 * (COUNT(d.id_d_matricula) - LAG(COUNT(d.id_d_matricula)) OVER (ORDER BY p.fecha_inicio)) " +
            "       / NULLIF(LAG(COUNT(d.id_d_matricula)) OVER (ORDER BY p.fecha_inicio), 0), 2) AS variacion_porcentual " +
            " FROM periodos_academicos p LEFT JOIN detalles_matricula d ON d.id_periodo = p.id_periodo " +
            " GROUP BY p.id_periodo, p.nombre, p.fecha_inicio, p.estado " +
            " ORDER BY p.fecha_inicio", nativeQuery = true)
    List<Object[]> evolucionDeMatricula();

    // Decisión: si muchos se matriculan cuando el periodo ya empezó, adelantar la campaña y nivelar a los tardíos.
    // Tablas: periodos_academicos + detalles_matricula.
    @Query(value = "SELECT p.nombre, p.fecha_inicio, COUNT(d.id_d_matricula) AS total, " +
            " SUM(CASE WHEN d.fecha_matricula <= p.fecha_inicio THEN 1 ELSE 0 END) AS anticipadas, " +
            " SUM(CASE WHEN d.fecha_matricula > p.fecha_inicio AND d.fecha_matricula <= p.fecha_fin THEN 1 ELSE 0 END) AS tardias, " +
            " SUM(CASE WHEN d.fecha_matricula > p.fecha_fin THEN 1 ELSE 0 END) AS fuera_del_periodo, " +
            " ROUND(100.0 * SUM(CASE WHEN d.fecha_matricula > p.fecha_inicio THEN 1 ELSE 0 END) " +
            "       / COUNT(d.id_d_matricula), 2) AS porcentaje_tardias " +
            " FROM periodos_academicos p INNER JOIN detalles_matricula d ON d.id_periodo = p.id_periodo " +
            " WHERE d.fecha_matricula IS NOT NULL " +
            " GROUP BY p.id_periodo, p.nombre, p.fecha_inicio " +
            " ORDER BY porcentaje_tardias DESC, p.fecha_inicio DESC", nativeQuery = true)
    List<Object[]> matriculaTardiaPorPeriodo();

    // HU31/HU41: periodos cuyas fechas se cruzan con [inicio, fin], sin contar el que se está editando.
    @Query(value = """
            SELECT COUNT(*) FROM periodos_academicos
            WHERE id_periodo <> :excluir AND fecha_inicio <= :fin AND fecha_fin >= :inicio
            """, nativeQuery = true)
    long contarCruces(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin, @Param("excluir") Long excluir);

    // HU31: periodos ACTIVO aparte del que se está editando (solo puede haber uno).
    @Query(value = "SELECT COUNT(*) FROM periodos_academicos WHERE id_periodo <> :excluir AND UPPER(estado) = 'ACTIVO'", nativeQuery = true)
    long contarActivos(@Param("excluir") Long excluir);
}
