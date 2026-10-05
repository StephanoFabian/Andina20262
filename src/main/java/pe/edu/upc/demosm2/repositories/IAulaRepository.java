package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.entities.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAulaRepository extends JpaRepository<Aula,Long> {
}
