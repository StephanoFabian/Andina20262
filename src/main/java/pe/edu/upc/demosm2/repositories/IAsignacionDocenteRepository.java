package pe.edu.upc.demosm2.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.demosm2.dtos.AsignacionDTOList;
import pe.edu.upc.demosm2.entities.AsignacionDocente;

import java.util.List;

public interface IAsignacionDocenteRepository extends JpaRepository<AsignacionDocente,Long> {
public List<AsignacionDocente> findByHorassemanalesIsBetween(Long hora_min, Long hora_max);

    // HU07: ¿el curso ya tiene docente en esa aula, colegio y periodo? (sin contar la asignación que se edita)
    @Query(value = """
            SELECT COUNT(*) FROM asignacion_docente
            WHERE id_colegio = :idColegio AND id_curso = :idCurso AND id_periodo = :idPeriodo
              AND UPPER(TRIM(nombre_colegio)) = UPPER(TRIM(:aula)) AND id_asignacion <> :excluir
            """, nativeQuery = true)
    long contarConflictos(@Param("idColegio") Long idColegio, @Param("idCurso") Long idCurso, @Param("idPeriodo") Long idPeriodo,
                          @Param("aula") String aula, @Param("excluir") Long excluir);
}
