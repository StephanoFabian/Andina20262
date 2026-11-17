package pe.edu.upc.demosm2.repositories;
import org.springframework.data.repository.query.Param;

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
    List<Object[]> reporteMaterialesPorCurso();
    @Query(value="SELECT t.id_material,t.id_curso,m.titulo,c.nombre_curso FROM material_curso t join material m on m.id_material=t.id_material join curso c on c.id_curso=t.id_curso WHERE (:texto = '%' OR LOWER(m.titulo) LIKE :texto ESCAPE '!' OR LOWER(c.nombre_curso) LIKE :texto ESCAPE '!') AND (:idMaterial=0 OR t.id_material=:idMaterial) ORDER BY t.id_material,t.id_curso LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> pagina(@Param("texto") String texto,@Param("limite") int limite,@Param("desplazamiento") int desplazamiento, @Param("idMaterial") Long idMaterial);
    @Query(value="SELECT COUNT(*) FROM material_curso t join material m on m.id_material=t.id_material join curso c on c.id_curso=t.id_curso WHERE (:texto = '%' OR LOWER(m.titulo) LIKE :texto ESCAPE '!' OR LOWER(c.nombre_curso) LIKE :texto ESCAPE '!') AND (:idMaterial=0 OR t.id_material=:idMaterial)",nativeQuery=true)
    long contarPagina(@Param("texto") String texto, @Param("idMaterial") Long idMaterial);
    @Query(value="SELECT c.nombre_curso,COUNT(mc.id_material) FROM curso c LEFT JOIN material_curso mc ON mc.id_curso=c.id_curso GROUP BY c.id_curso,c.nombre_curso ORDER BY COUNT(mc.id_material),c.id_curso LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> cursosPagina(@Param("limite") int limite,@Param("desplazamiento") int desplazamiento);
    @Query(value="SELECT COUNT(*) FROM curso",nativeQuery=true)
    long contarCursos();
}
