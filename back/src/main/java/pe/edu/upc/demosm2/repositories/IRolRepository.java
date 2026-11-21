package pe.edu.upc.demosm2.repositories;
import org.springframework.data.repository.query.Param;

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
    @Query(value="SELECT t.id_tipo_persona,t.detalle_rol FROM rol t WHERE (:texto = '%' OR LOWER(t.detalle_rol) LIKE :texto ESCAPE '!') ORDER BY t.id_tipo_persona LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> pagina(@Param("texto") String texto,@Param("limite") int limite,@Param("desplazamiento") int desplazamiento);
    @Query(value="SELECT COUNT(*) FROM rol t WHERE (:texto = '%' OR LOWER(t.detalle_rol) LIKE :texto ESCAPE '!')",nativeQuery=true)
    long contarPagina(@Param("texto") String texto);
}
