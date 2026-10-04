package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.dtos.CuentasPorRolQuery;
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
    List<CuentasPorRolQuery> reporteCuentasPorRol();
}
