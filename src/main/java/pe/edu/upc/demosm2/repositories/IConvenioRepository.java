package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.entities.Convenio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IConvenioRepository extends JpaRepository<Convenio, Long> {

    // Decisión (HU11): qué convenios renovar primero: los vencidos y los que vencen dentro de :dias días
    // (los FINALIZADO ya no cuentan). Tabla: convenio. El que vence antes sale primero.
    @Query(value = """
            SELECT id_convenio, nombre, entidad, region, fecha_fin,
                   (fecha_fin - CURRENT_DATE) AS diasRestantes, presupuesto, estado,
                   CASE WHEN fecha_fin < CURRENT_DATE THEN 'VENCIDO' ELSE 'POR_VENCER' END AS situacion
            FROM convenio
            WHERE UPPER(estado) <> 'FINALIZADO' AND fecha_fin <= CURRENT_DATE + :dias
            ORDER BY fecha_fin, nombre
            """, nativeQuery = true)
    List<Object[]> reportePorVencer(@Param("dias") Integer dias);

    // HU11: convenios activos hoy (estado VIGENTE y sin pasar su fecha de fin). El que vence antes sale primero.
    @Query("""
            SELECT c FROM Convenio c
            WHERE UPPER(c.estado) = 'VIGENTE' AND c.fechaFin >= CURRENT_DATE
            ORDER BY c.fechaFin, c.nombre
            """)
    List<Convenio> listarVigentes();

    // HU11: cuántos colegios financia cada convenio, filtrando por región y/o tipo de entidad
    // (un filtro vacío no filtra). Tablas: convenio + colegios. Los que cubren más colegios salen primero.
    @Query(value = """
            SELECT c.id_convenio, c.nombre, c.entidad, c.tipo_entidad, c.region, c.estado, c.fecha_fin,
                   COUNT(co.id_colegio) AS cantidadColegios
            FROM convenio c
            LEFT JOIN colegios co ON co.id_convenio = c.id_convenio
            WHERE (:region = '' OR UPPER(c.region) = UPPER(:region))
              AND (:tipoEntidad = '' OR UPPER(c.tipo_entidad) = UPPER(:tipoEntidad))
            GROUP BY c.id_convenio, c.nombre, c.entidad, c.tipo_entidad, c.region, c.estado, c.fecha_fin
            ORDER BY cantidadColegios DESC, c.nombre
            """, nativeQuery = true)
    List<Object[]> colegiosPorConvenio(@Param("region") String region, @Param("tipoEntidad") String tipoEntidad);
}
