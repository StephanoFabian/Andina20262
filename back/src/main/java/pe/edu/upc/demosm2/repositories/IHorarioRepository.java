package pe.edu.upc.demosm2.repositories;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.demosm2.entities.Horario;
public interface IHorarioRepository extends JpaRepository<Horario,Long> {     @Query(value="SELECT t.id_horario,t.dia_semana,t.hora_inicio,t.hora_fin,t.id_asignacion FROM horarios t join asignacion_docente a on a.id_asignacion=t.id_asignacion WHERE (:texto = '%' OR LOWER(a.modalidad) LIKE :texto ESCAPE '!') ORDER BY t.id_horario LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> pagina(@Param("texto") String texto,@Param("limite") int limite,@Param("desplazamiento") int desplazamiento);
    @Query(value="SELECT COUNT(*) FROM horarios t join asignacion_docente a on a.id_asignacion=t.id_asignacion WHERE (:texto = '%' OR LOWER(a.modalidad) LIKE :texto ESCAPE '!')",nativeQuery=true)
    long contarPagina(@Param("texto") String texto);
    @Query(value="SELECT COUNT(*) FROM horarios WHERE id_asignacion=:idAsignacion AND dia_semana=:dia AND hora_inicio<:fin AND hora_fin>:inicio AND (COALESCE(CAST(:id AS BIGINT),0)=0 OR id_horario<>:id)",nativeQuery=true)
    Long contarCruces(@Param("idAsignacion") Long idAsignacion,@Param("dia") Integer dia,@Param("inicio") java.time.LocalTime inicio,@Param("fin") java.time.LocalTime fin,@Param("id") Long id);
}
