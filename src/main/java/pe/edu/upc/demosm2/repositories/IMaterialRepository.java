package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.entities.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMaterialRepository extends JpaRepository<Material, Long> {

    // Buscar materiales por parte del título
    @Query(value = """
            SELECT *
            FROM material
            WHERE LOWER(titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))
            ORDER BY fecha_publicacion DESC
            """, nativeQuery = true)
    List<Material> buscarPorTitulo(@Param("titulo") String titulo);

    // Materiales asignados a un curso (tablas material + material_curso)
    @Query(value = """
            SELECT m.*
            FROM material m
            INNER JOIN material_curso mc ON mc.id_material = m.id_material
            WHERE mc.id_curso = :idCurso
            ORDER BY m.fecha_publicacion DESC
            """, nativeQuery = true)
    List<Material> listarMaterialesPorCurso(@Param("idCurso") Long idCurso);

    // Reporte: cuántos materiales hay de cada tipo
    @Query(value = """
            SELECT LOWER(tipo), COUNT(id_material)
            FROM material
            GROUP BY LOWER(tipo)
            ORDER BY COUNT(id_material) DESC
            """, nativeQuery = true)
    List<Object[]> reporteMaterialesPorTipo();
}
