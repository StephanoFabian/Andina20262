package pe.edu.upc.demosm2.repositories;
import org.springframework.data.repository.query.Param;

import pe.edu.upc.demosm2.entities.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPersonaRepositories extends JpaRepository<Persona, Long> {

    // Decisión: a qué roles hay que activarles cuentas o ponerles contraseña (sin contraseña no pueden entrar).
    // Tablas: persona + rol.
    @Query(value = """
            SELECT r.detalle_rol AS rol,
                   COUNT(p.id_persona) AS total,
                   SUM(CASE WHEN UPPER(p.estado_persona) = 'ACTIVO' THEN 1 ELSE 0 END) AS activos,
                   SUM(CASE WHEN UPPER(p.estado_persona) <> 'ACTIVO' THEN 1 ELSE 0 END) AS inactivos,
                   SUM(CASE WHEN p.password_persona IS NULL THEN 1 ELSE 0 END) AS sinPassword
            FROM persona p
            INNER JOIN rol r ON r.id_tipo_persona = p.id_tipo_persona
            GROUP BY r.id_tipo_persona, r.detalle_rol
            ORDER BY sinPassword DESC, inactivos DESC, total DESC
            """, nativeQuery = true)
    List<Object[]> reporteCuentasPorRol();
    @Query(value="SELECT t.id_persona,t.nombres_persona,t.apellidos_persona,t.fecha_nacimiento_persona,t.email_persona,t.estado_persona,t.id_tipo_persona FROM persona t WHERE (:texto = '%' OR LOWER(t.nombres_persona) LIKE :texto ESCAPE '!' OR LOWER(t.apellidos_persona) LIKE :texto ESCAPE '!' OR LOWER(t.email_persona) LIKE :texto ESCAPE '!') ORDER BY t.id_persona LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> pagina(@Param("texto") String texto,@Param("limite") int limite,@Param("desplazamiento") int desplazamiento);
    @Query(value="SELECT COUNT(*) FROM persona t WHERE (:texto = '%' OR LOWER(t.nombres_persona) LIKE :texto ESCAPE '!' OR LOWER(t.apellidos_persona) LIKE :texto ESCAPE '!' OR LOWER(t.email_persona) LIKE :texto ESCAPE '!')",nativeQuery=true)
    long contarPagina(@Param("texto") String texto);
}
