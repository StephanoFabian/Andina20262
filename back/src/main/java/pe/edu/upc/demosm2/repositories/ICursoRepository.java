package pe.edu.upc.demosm2.repositories;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.demosm2.entities.Curso;

import java.util.List;

public interface ICursoRepository extends JpaRepository<Curso,Long> {
   List<Curso> findByArea(String area);
    @Query(value="SELECT t.id_curso,t.nombre_curso,t.descripcion,t.area FROM curso t WHERE (:texto = '%' OR LOWER(t.nombre_curso) LIKE :texto ESCAPE '!' OR LOWER(t.area) LIKE :texto ESCAPE '!') ORDER BY t.id_curso LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> pagina(@Param("texto") String texto,@Param("limite") int limite,@Param("desplazamiento") int desplazamiento);
    @Query(value="SELECT COUNT(*) FROM curso t WHERE (:texto = '%' OR LOWER(t.nombre_curso) LIKE :texto ESCAPE '!' OR LOWER(t.area) LIKE :texto ESCAPE '!')",nativeQuery=true)
    long contarPagina(@Param("texto") String texto);
}
