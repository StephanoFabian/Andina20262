package com.andina.plataforma.repository;

import com.andina.plataforma.model.entity.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {

    Page<Material> findByPersonaIdPersona(Integer idPersona, Pageable pageable);

    Page<Material> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    Page<Material> findByTipo(String tipo, Pageable pageable);
}