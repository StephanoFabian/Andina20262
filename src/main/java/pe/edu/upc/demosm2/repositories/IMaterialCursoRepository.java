package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.entities.MaterialCurso;
import pe.edu.upc.demosm2.entities.MaterialCursoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMaterialCursoRepository extends JpaRepository<MaterialCurso, MaterialCursoId> {
}
