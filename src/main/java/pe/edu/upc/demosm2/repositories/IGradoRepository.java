package pe.edu.upc.demosm2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.demosm2.entities.Grado;

@Repository
public interface IGradoRepository extends JpaRepository<Grado, Long> {
}
