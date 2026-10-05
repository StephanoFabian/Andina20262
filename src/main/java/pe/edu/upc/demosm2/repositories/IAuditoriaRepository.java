package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.entities.Auditoria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IAuditoriaRepository extends JpaRepository<Auditoria, Long> {

    // HU15: bitácora con filtros opcionales (un filtro vacío no filtra), la más reciente primero y por páginas,
    // porque la tabla crece con cada acceso y no se debe traer completa. El rango de fechas siempre llega lleno.
    @Query("""
            SELECT a FROM Auditoria a
            WHERE (:usuario IS NULL OR a.usuario = :usuario)
              AND (:modulo IS NULL OR a.modulo = :modulo)
              AND (:accion IS NULL OR a.accion = :accion)
              AND a.fechaHora >= :desde
              AND a.fechaHora < :hasta
            ORDER BY a.fechaHora DESC, a.idAuditoria DESC
            """)
    List<Auditoria> filtrar(@Param("usuario") String usuario, @Param("modulo") String modulo, @Param("accion") String accion,
                            @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta, Pageable pageable);

    // Decisión (HU15): qué cuentas revisar o bloquear por intentos de acceso fallidos en los últimos :dias días.
    @Query(value = """
            SELECT usuario, COUNT(*) AS intentosFallidos, MAX(fecha_hora) AS ultimoIntento, COUNT(DISTINCT ip) AS ipsDistintas
            FROM auditoria
            WHERE accion = 'LOGIN_FALLIDO' AND fecha_hora >= LOCALTIMESTAMP - (:dias * INTERVAL '1 day')
            GROUP BY usuario
            ORDER BY intentosFallidos DESC, ultimoIntento DESC
            LIMIT 100
            """, nativeQuery = true)
    List<Object[]> reporteLoginFallidos(@Param("dias") Integer dias);
}
