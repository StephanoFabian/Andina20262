package pe.edu.upc.demosm2.repositories;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.demosm2.entities.Asistencia;
public interface IAsistenciaRepository extends JpaRepository<Asistencia,Long> {     @Query(value="SELECT t.id_asistencia,t.estado,t.observacion,t.id_sesion_virtual,t.id_persona FROM asistencias t join persona p on p.id_persona=t.id_persona WHERE (:texto = '%' OR LOWER(t.estado) LIKE :texto ESCAPE '!' OR LOWER(p.nombres_persona) LIKE :texto ESCAPE '!') ORDER BY t.id_asistencia LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> pagina(@Param("texto") String texto,@Param("limite") int limite,@Param("desplazamiento") int desplazamiento);
    @Query(value="SELECT COUNT(*) FROM asistencias t join persona p on p.id_persona=t.id_persona WHERE (:texto = '%' OR LOWER(t.estado) LIKE :texto ESCAPE '!' OR LOWER(p.nombres_persona) LIKE :texto ESCAPE '!')",nativeQuery=true)
    long contarPagina(@Param("texto") String texto);
}
