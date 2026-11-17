package pe.edu.upc.demosm2.repositories;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import pe.edu.upc.demosm2.entities.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAulaRepository extends JpaRepository<Aula,Long> {
    @Query(value="SELECT t.id_aula,t.nombre,t.seccion,t.capacidad,t.id_colegio FROM aulas t WHERE (:texto = '%' OR LOWER(t.nombre) LIKE :texto ESCAPE '!' OR LOWER(t.seccion) LIKE :texto ESCAPE '!') ORDER BY t.id_aula LIMIT :limite OFFSET :desplazamiento",nativeQuery=true)
    java.util.List<Object[]> pagina(@Param("texto") String texto,@Param("limite") int limite,@Param("desplazamiento") int desplazamiento);
    @Query(value="SELECT COUNT(*) FROM aulas t WHERE (:texto = '%' OR LOWER(t.nombre) LIKE :texto ESCAPE '!' OR LOWER(t.seccion) LIKE :texto ESCAPE '!')",nativeQuery=true)
    long contarPagina(@Param("texto") String texto);
}
