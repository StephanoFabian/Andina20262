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

    // Decisión: qué tipo de material falta (los tipos con menos materiales salen al final).
    @Query(value = """
            SELECT LOWER(tipo) AS tipo, COUNT(id_material) AS cantidad
            FROM material
            GROUP BY LOWER(tipo)
            ORDER BY cantidad DESC
            """, nativeQuery = true)
    List<Object[]> reporteMaterialesPorTipo();
    @Query(value="SELECT t.id_material,t.titulo,t.descripcion,t.tipo,t.url_archivo,t.fecha_publicacion,t.id_persona,p.nombres_persona,p.apellidos_persona FROM material t join persona p on p.id_persona=t.id_persona WHERE (:texto = '%' OR LOWER(t.titulo) LIKE :texto ESCAPE '!' OR LOWER(t.tipo) LIKE :texto ESCAPE '!' OR LOWER(p.nombres_persona) LIKE :texto ESCAPE '!' OR LOWER(p.apellidos_persona) LIKE :texto ESCAPE '!') ORDER BY t.id_material LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> pagina(@Param("texto") String texto,@Param("limite") int limite,@Param("desplazamiento") int desplazamiento);
    @Query(value="SELECT COUNT(*) FROM material t join persona p on p.id_persona=t.id_persona WHERE (:texto = '%' OR LOWER(t.titulo) LIKE :texto ESCAPE '!' OR LOWER(t.tipo) LIKE :texto ESCAPE '!' OR LOWER(p.nombres_persona) LIKE :texto ESCAPE '!' OR LOWER(p.apellidos_persona) LIKE :texto ESCAPE '!')",nativeQuery=true)
    long contarPagina(@Param("texto") String texto);
    @Query(value="SELECT DISTINCT tipo FROM material WHERE tipo IS NOT NULL AND LOWER(tipo) LIKE :texto ESCAPE '!' ORDER BY tipo LIMIT 20",nativeQuery=true)
    java.util.List<String> tiposSugeridos(@Param("texto") String texto);
    @Query(value="SELECT t.id_material,t.titulo,t.descripcion,t.tipo,t.url_archivo,t.fecha_publicacion,t.id_persona FROM material t WHERE (:texto='%' OR LOWER(t.titulo) LIKE :texto ESCAPE '!') AND (:idCurso=0 OR EXISTS(SELECT 1 FROM material_curso mc WHERE mc.id_material=t.id_material AND mc.id_curso=:idCurso)) ORDER BY t.id_material LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> consultaPagina(@Param("texto") String texto,@Param("idCurso") Long idCurso,@Param("limite") int limite,@Param("desplazamiento") int desplazamiento);
    @Query(value="SELECT COUNT(*) FROM material t WHERE (:texto='%' OR LOWER(t.titulo) LIKE :texto ESCAPE '!') AND (:idCurso=0 OR EXISTS(SELECT 1 FROM material_curso mc WHERE mc.id_material=t.id_material AND mc.id_curso=:idCurso))",nativeQuery=true)
    long contarConsulta(@Param("texto") String texto,@Param("idCurso") Long idCurso);
    @Query(value="SELECT tipo,COUNT(*) FROM material GROUP BY tipo ORDER BY tipo NULLS FIRST LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> tiposPagina(@Param("limite") int limite,@Param("desplazamiento") int desplazamiento);
    @Query(value="SELECT COUNT(*) FROM (SELECT tipo FROM material GROUP BY tipo) t",nativeQuery=true)
    long contarTipos();
}
