package pe.edu.upc.demosm2.repositories;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.demosm2.entities.SesionVirtual;
public interface ISesionVirtualRepository extends JpaRepository<SesionVirtual,Long> {     @Query(value="SELECT t.id_sesion_virtual,t.titulo,t.fecha,t.hora_inicio,t.hora_fin,t.enlace,t.estado,t.id_asignacion FROM sesiones_virtuales t WHERE (:texto = '%' OR LOWER(t.titulo) LIKE :texto ESCAPE '!' OR LOWER(t.estado) LIKE :texto ESCAPE '!') ORDER BY t.id_sesion_virtual LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> pagina(@Param("texto") String texto,@Param("limite") int limite,@Param("desplazamiento") int desplazamiento);
    @Query(value="SELECT COUNT(*) FROM sesiones_virtuales t WHERE (:texto = '%' OR LOWER(t.titulo) LIKE :texto ESCAPE '!' OR LOWER(t.estado) LIKE :texto ESCAPE '!')",nativeQuery=true)
    long contarPagina(@Param("texto") String texto);
}
