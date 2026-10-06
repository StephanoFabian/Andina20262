package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.entities.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface IAulaRepository extends JpaRepository<Aula,Long> {
    @EntityGraph(attributePaths = "colegio")
    Slice<Aula> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = "colegio")
    Slice<Aula> findByColegio_IdColegio(Long idColegio, Pageable pageable);
}
