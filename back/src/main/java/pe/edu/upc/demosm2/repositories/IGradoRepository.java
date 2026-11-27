package pe.edu.upc.demosm2.repositories;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.demosm2.entities.Grado;

@Repository
public interface IGradoRepository extends JpaRepository<Grado, Long> {
    @Query(value="SELECT t.id_grado,t.nombre,t.nivel FROM grados t WHERE (:texto = '%' OR LOWER(t.nombre) LIKE :texto ESCAPE '!' OR LOWER(t.nivel) LIKE :texto ESCAPE '!') ORDER BY t.id_grado LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> pagina(@Param("texto") String texto,@Param("limite") int limite,@Param("desplazamiento") int desplazamiento);
    @Query(value="SELECT COUNT(*) FROM grados t WHERE (:texto = '%' OR LOWER(t.nombre) LIKE :texto ESCAPE '!' OR LOWER(t.nivel) LIKE :texto ESCAPE '!')",nativeQuery=true)
    long contarPagina(@Param("texto") String texto);
}
