package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.entities.RespuestaEncuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRespuestaEncuestaRepository extends JpaRepository<RespuestaEncuesta, Long> {

    // HU16: respuestas de una encuesta, la más reciente primero
    @Query(value = "SELECT * FROM respuesta_encuesta WHERE id_encuesta = :idEncuesta ORDER BY fecha_respuesta DESC",
            nativeQuery = true)
    List<RespuestaEncuesta> listarPorEncuesta(@Param("idEncuesta") Long idEncuesta);

    // HU16: cada persona responde una sola vez cada encuesta
    @Query(value = "SELECT COUNT(*) FROM respuesta_encuesta WHERE id_encuesta = :idEncuesta AND id_persona = :idPersona",
            nativeQuery = true)
    long contarRespuestas(@Param("idEncuesta") Long idEncuesta, @Param("idPersona") Long idPersona);

    // Decisión (HU16): qué perfil (estudiantes, docentes locales, especialistas…) está menos satisfecho.
    // Tablas: respuesta_encuesta + persona + rol.
    @Query(value = """
            SELECT ro.detalle_rol AS rol, COUNT(r.id_respuesta) AS respuestas,
                   ROUND(AVG(r.satisfaccion_general), 2) AS promedioSatisfaccion,
                   ROUND(AVG(r.calidad_contenido), 2) AS promedioContenido,
                   ROUND(AVG(r.facilidad_uso), 2) AS promedioFacilidad,
                   ROUND(100.0 * SUM(CASE WHEN r.satisfaccion_general >= 4 THEN 1 ELSE 0 END)
                         / COUNT(r.id_respuesta), 2) AS porcentajeSatisfechos
            FROM respuesta_encuesta r
            INNER JOIN persona p ON p.id_persona = r.id_persona
            INNER JOIN rol ro ON ro.id_tipo_persona = p.id_tipo_persona
            WHERE r.id_encuesta = :idEncuesta
            GROUP BY ro.id_tipo_persona, ro.detalle_rol
            ORDER BY promedioSatisfaccion ASC, ro.detalle_rol
            """, nativeQuery = true)
    List<Object[]> reporteSatisfaccionPorRol(@Param("idEncuesta") Long idEncuesta);
}
