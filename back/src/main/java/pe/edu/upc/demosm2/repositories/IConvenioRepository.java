package pe.edu.upc.demosm2.repositories;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.demosm2.entities.Convenio;
public interface IConvenioRepository extends JpaRepository<Convenio,Long> {     @Query(value="SELECT t.id_convenio,t.nombre,t.institucion,t.institucion,t.tipo_entidad,t.region,t.presupuesto,t.fecha_inicio,t.fecha_fin,t.estado,t.id_colegio FROM convenios t WHERE (:texto = '%' OR LOWER(t.nombre) LIKE :texto ESCAPE '!' OR LOWER(t.institucion) LIKE :texto ESCAPE '!' OR LOWER(t.region) LIKE :texto ESCAPE '!' OR LOWER(t.estado) LIKE :texto ESCAPE '!') ORDER BY t.id_convenio LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> pagina(@Param("texto") String texto,@Param("limite") int limite,@Param("desplazamiento") int desplazamiento);
    @Query(value="SELECT COUNT(*) FROM convenios t WHERE (:texto = '%' OR LOWER(t.nombre) LIKE :texto ESCAPE '!' OR LOWER(t.institucion) LIKE :texto ESCAPE '!' OR LOWER(t.region) LIKE :texto ESCAPE '!' OR LOWER(t.estado) LIKE :texto ESCAPE '!')",nativeQuery=true)
    long contarPagina(@Param("texto") String texto);
    @Query(value="SELECT id_convenio,nombre,institucion,region,fecha_fin,presupuesto,estado,(fecha_fin-CURRENT_DATE) FROM convenios WHERE fecha_fin <= CURRENT_DATE + CAST(:dias AS INTEGER) AND estado IN ('VIGENTE','EN_RENOVACION','VENCIDO') ORDER BY fecha_fin,id_convenio LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> vencimientosPagina(@Param("dias") int dias,@Param("limite") int limite,@Param("desplazamiento") int desplazamiento);
    @Query(value="SELECT COUNT(*) FROM convenios WHERE fecha_fin <= CURRENT_DATE + CAST(:dias AS INTEGER) AND estado IN ('VIGENTE','EN_RENOVACION','VENCIDO')",nativeQuery=true)
    long contarVencimientos(@Param("dias") int dias);
}
