package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.entities.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPersonaRepositories extends JpaRepository<Persona, Long> {
}
