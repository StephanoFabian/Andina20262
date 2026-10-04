package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.dtos.CursoMaterialesQuery;
import pe.edu.upc.demosm2.entities.MaterialCurso;
import pe.edu.upc.demosm2.entities.MaterialCursoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMaterialCursoRepository extends JpaRepository<MaterialCurso, MaterialCursoId> {

    // Decisión: a qué cursos hay que subirles materiales primero (los que tienen menos salen arriba).
    // Tablas: curso + material_curso (incluye cursos sin ningún material).
    @Query(value = """
            SELECT c.nombre_curso AS curso, COUNT(mc.id_material) AS cantidadMateriales
            FROM curso c
            LEFT JOIN material_curso mc ON mc.id_curso = c.id_curso
            GROUP BY c.id_curso, c.nombre_curso
            ORDER BY cantidadMateriales ASC, c.nombre_curso
            """, nativeQuery = true)
    List<CursoMaterialesQuery> reporteMaterialesPorCurso();
}
