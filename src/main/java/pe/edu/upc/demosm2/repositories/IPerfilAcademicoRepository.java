package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.entities.PerfilAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPerfilAcademicoRepository extends JpaRepository<PerfilAcademico, Long> {
}
