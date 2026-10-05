package pe.edu.upc.demosm2.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.edu.upc.demosm2.entities.Colegio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@Repository
public interface IColegioRepository extends JpaRepository<Colegio,Long> {
    Slice<Colegio> findAllBy(Pageable pageable);

    // Decisión (HU06): a qué escuelas llevar primero mejoras de conectividad: las que están bajo el mínimo
    // y las que aún no tienen medición, con más estudiantes afectados arriba. Tablas: colegios + matriculas.
    @Query(value = """
            SELECT c.id_colegio, c.nombre, c.departamento, c.tipo_zona, c.tipo_conexion,
                   c.velocidad_bajada_mbps, c.velocidad_subida_mbps, COUNT(m.id_matricula) AS estudiantes,
                   CASE WHEN c.velocidad_bajada_mbps IS NULL OR c.velocidad_subida_mbps IS NULL THEN 'SIN_MEDICION'
                        WHEN c.velocidad_bajada_mbps < :bajadaMinima OR c.velocidad_subida_mbps < :subidaMinima THEN 'INSUFICIENTE'
                        ELSE 'ADECUADA' END AS estado
            FROM colegios c
            LEFT JOIN matriculas m ON m.id_colegio = c.id_colegio
            GROUP BY c.id_colegio, c.nombre, c.departamento, c.tipo_zona, c.tipo_conexion,
                     c.velocidad_bajada_mbps, c.velocidad_subida_mbps
            ORDER BY CASE WHEN c.velocidad_bajada_mbps IS NULL OR c.velocidad_subida_mbps IS NULL THEN 2
                          WHEN c.velocidad_bajada_mbps < :bajadaMinima OR c.velocidad_subida_mbps < :subidaMinima THEN 1
                          ELSE 3 END,
                     estudiantes DESC, c.nombre
            """, nativeQuery = true)
    List<Object[]> reporteConectividad(@Param("bajadaMinima") Double bajadaMinima, @Param("subidaMinima") Double subidaMinima);
}
