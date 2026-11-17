package pe.edu.upc.demosm2.repositories;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import pe.edu.upc.demosm2.entities.Colegio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IColegioRepository extends JpaRepository<Colegio,Long> {

    @Query(value="SELECT t.id_colegio,t.nombre,t.departamento,t.provincia,t.distrito,t.comunidad,t.tipo_zona FROM colegios t WHERE (:texto = '%' OR LOWER(t.nombre) LIKE :texto ESCAPE '!' OR LOWER(t.departamento) LIKE :texto ESCAPE '!' OR LOWER(t.distrito) LIKE :texto ESCAPE '!') ORDER BY t.id_colegio LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> pagina(@Param("texto") String texto,@Param("limite") int limite,@Param("desplazamiento") int desplazamiento);
    @Query(value="SELECT COUNT(*) FROM colegios t WHERE (:texto = '%' OR LOWER(t.nombre) LIKE :texto ESCAPE '!' OR LOWER(t.departamento) LIKE :texto ESCAPE '!' OR LOWER(t.distrito) LIKE :texto ESCAPE '!')",nativeQuery=true)
    long contarPagina(@Param("texto") String texto);
}
