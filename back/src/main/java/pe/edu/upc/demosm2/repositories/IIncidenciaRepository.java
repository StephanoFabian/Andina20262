package pe.edu.upc.demosm2.repositories;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.demosm2.entities.Incidencia;
public interface IIncidenciaRepository extends JpaRepository<Incidencia,Long> {     @Query(value="SELECT t.id_incidencia,t.titulo,t.descripcion,t.fecha,t.estado,t.prioridad,t.id_persona,t.id_colegio FROM incidencias t WHERE (:texto = '%' OR LOWER(t.titulo) LIKE :texto ESCAPE '!' OR LOWER(t.estado) LIKE :texto ESCAPE '!' OR LOWER(t.prioridad) LIKE :texto ESCAPE '!') ORDER BY t.id_incidencia LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> pagina(@Param("texto") String texto,@Param("limite") int limite,@Param("desplazamiento") int desplazamiento);
    @Query(value="SELECT COUNT(*) FROM incidencias t WHERE (:texto = '%' OR LOWER(t.titulo) LIKE :texto ESCAPE '!' OR LOWER(t.estado) LIKE :texto ESCAPE '!' OR LOWER(t.prioridad) LIKE :texto ESCAPE '!')",nativeQuery=true)
    long contarPagina(@Param("texto") String texto);
}
