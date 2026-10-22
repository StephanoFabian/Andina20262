package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.entities.Encuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEncuestaRepository extends JpaRepository<Encuesta, Long> {

    // Decisión (HU16): qué ciclos tuvieron baja participación o baja satisfacción para revisarlos primero.
    // Promedio general = media de las tres preguntas Likert; satisfecho = satisfacción general de 4 o 5.
    // Tablas: encuesta + respuesta_encuesta (incluye encuestas sin respuestas).
    @Query(value = """
            SELECT e.id_encuesta, e.titulo, e.estado, COUNT(r.id_respuesta) AS respuestas,
                   ROUND(AVG((r.satisfaccion_general + r.calidad_contenido + r.facilidad_uso) / 3.0), 2) AS promedioGeneral,
                   ROUND(100.0 * SUM(CASE WHEN r.satisfaccion_general >= 4 THEN 1 ELSE 0 END)
                         / NULLIF(COUNT(r.id_respuesta), 0), 2) AS porcentajeSatisfechos
            FROM encuesta e
            LEFT JOIN respuesta_encuesta r ON r.id_encuesta = e.id_encuesta
            GROUP BY e.id_encuesta, e.titulo, e.estado
            ORDER BY promedioGeneral ASC NULLS FIRST, respuestas ASC, e.id_encuesta
            """, nativeQuery = true)
    List<Object[]> reporteParticipacion();
}
