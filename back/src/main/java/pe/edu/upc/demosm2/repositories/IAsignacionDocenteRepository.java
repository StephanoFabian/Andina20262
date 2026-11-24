package pe.edu.upc.demosm2.repositories;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.demosm2.dtos.AsignacionDTOList;
import pe.edu.upc.demosm2.entities.AsignacionDocente;

import java.util.List;

public interface IAsignacionDocenteRepository extends JpaRepository<AsignacionDocente,Long> {
public List<AsignacionDocente> findByHorassemanalesIsBetween(Long hora_min, Long hora_max);
    @Query(value="SELECT t.id_asignacion,t.nombre_colegio,t.id_curso,t.id_periodo,t.id_persona,t.id_colegio,t.modalidad,t.horas_semanales FROM asignacion_docente t WHERE (:texto = '%' OR LOWER(t.nombre_colegio) LIKE :texto ESCAPE '!' OR LOWER(t.modalidad) LIKE :texto ESCAPE '!') ORDER BY t.id_asignacion LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> pagina(@Param("texto") String texto,@Param("limite") int limite,@Param("desplazamiento") int desplazamiento);
    @Query(value="SELECT COUNT(*) FROM asignacion_docente t WHERE (:texto = '%' OR LOWER(t.nombre_colegio) LIKE :texto ESCAPE '!' OR LOWER(t.modalidad) LIKE :texto ESCAPE '!')",nativeQuery=true)
    long contarPagina(@Param("texto") String texto);
}
