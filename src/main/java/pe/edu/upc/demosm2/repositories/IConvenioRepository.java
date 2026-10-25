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
}
