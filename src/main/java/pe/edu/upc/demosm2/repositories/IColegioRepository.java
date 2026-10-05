package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.entities.Colegio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IColegioRepository extends JpaRepository<Colegio,Long> {

}
