package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRolRepository extends JpaRepository<Rol, Long> {

    // Decisión: ¿falta personal de algún tipo? Cuántas personas tiene cada rol y su porcentaje del total.
    // Tablas: rol + persona (incluye roles sin nadie asignado).
    @Query(value = """
            SELECT r.detalle_rol AS rol,
                   COUNT(p.id_persona) AS cantidad,
                   ROUND(100.0 * COUNT(p.id_persona) / NULLIF((SELECT COUNT(*) FROM persona), 0), 2) AS porcentaje
            FROM rol r
            LEFT JOIN persona p ON p.id_tipo_persona = r.id_tipo_persona
            GROUP BY r.id_tipo_persona, r.detalle_rol
            ORDER BY cantidad DESC
            """, nativeQuery = true)
    List<Object[]> reporteCantidadPersonasPorRol();
}
