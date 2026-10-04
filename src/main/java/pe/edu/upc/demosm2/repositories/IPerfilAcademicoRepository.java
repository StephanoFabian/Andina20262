package pe.edu.upc.demosm2.repositories;

import pe.edu.upc.demosm2.entities.PerfilAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPerfilAcademicoRepository extends JpaRepository<PerfilAcademico, Long> {

    // Perfiles con nota menor a la indicada (para saber a quién apoyar), de la nota más baja a la más alta
    @Query(value = """
            SELECT *
            FROM perfil_academico
            WHERE notaspa < :nota
            ORDER BY notaspa ASC
            """, nativeQuery = true)
    List<PerfilAcademico> listarPerfilesConNotaMenorA(@Param("nota") Double nota);
}
