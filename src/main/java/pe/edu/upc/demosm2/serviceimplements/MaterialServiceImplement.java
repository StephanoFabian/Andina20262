package com.andina.plataforma.service;

import com.andina.plataforma.dto.MaterialRequestDTO;
import com.andina.plataforma.dto.MaterialResponseDTO;
import com.andina.plataforma.exception.CursoNoEncontradoException;
import com.andina.plataforma.exception.MaterialNoEncontradoException;
import com.andina.plataforma.exception.PersonaNoEncontradoException;
import com.andina.plataforma.mapper.MaterialMapper;
import com.andina.plataforma.model.entity.Curso;
import com.andina.plataforma.model.entity.Material;
import com.andina.plataforma.model.entity.Persona;
import com.andina.plataforma.repository.CursoRepository;
import com.andina.plataforma.repository.MaterialRepository;
import com.andina.plataforma.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;
    private final PersonaRepository personaRepository;
    private final CursoRepository cursoRepository;
    private final MaterialMapper materialMapper;

    @Override
    @Transactional
    public MaterialResponseDTO crear(MaterialRequestDTO dto) {
        Persona persona = personaRepository.findById(dto.getIdPersona())
                .orElseThrow(() -> PersonaNoEncontradoException.conId(dto.getIdPersona()));

        Set<Curso> cursos = resolverCursos(dto.getIdsCursos());

        Material material = materialMapper.toEntity(dto);
        material.setPersona(persona);
        material.setCursos(cursos);

        Material saved = materialRepository.save(material);
        return materialMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MaterialResponseDTO obtenerPorId(Integer id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> MaterialNoEncontradoException.conId(id));
        return materialMapper.toResponseDTO(material);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaterialResponseDTO> obtenerTodos(Pageable pageable) {
        return materialRepository.findAll(pageable)
                .map(materialMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaterialResponseDTO> obtenerPorPersona(Integer idPersona, Pageable pageable) {
        return materialRepository.findByPersonaIdPersona(idPersona, pageable)
                .map(materialMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public MaterialResponseDTO actualizar(Integer id, MaterialRequestDTO dto) {
        Material existing = materialRepository.findById(id)
                .orElseThrow(() -> MaterialNoEncontradoException.conId(id));

        Persona persona = personaRepository.findById(dto.getIdPersona())
                .orElseThrow(() -> PersonaNoEncontradoException.conId(dto.getIdPersona()));

        Set<Curso> cursos = resolverCursos(dto.getIdsCursos());

        materialMapper.updateEntityFromDTO(dto, existing);
        existing.setPersona(persona);
        existing.setCursos(cursos);

        Material updated = materialRepository.save(existing);
        return materialMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        if (!materialRepository.existsById(id)) {
            throw MaterialNoEncontradoException.conId(id);
        }
        materialRepository.deleteById(id);
    }

    @Override
    @Transactional
    public MaterialResponseDTO agregarCursoAMaterial(Integer idMaterial, Integer idCurso) {
        Material material = materialRepository.findById(idMaterial)
                .orElseThrow(() -> MaterialNoEncontradoException.conId(idMaterial));

        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> CursoNoEncontradoException.conId(idCurso));

        material.getCursos().add(curso);
        Material saved = materialRepository.save(material);
        return materialMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public MaterialResponseDTO quitarCursoDeMaterial(Integer idMaterial, Integer idCurso) {
        Material material = materialRepository.findById(idMaterial)
                .orElseThrow(() -> MaterialNoEncontradoException.conId(idMaterial));

        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> CursoNoEncontradoException.conId(idCurso));

        material.getCursos().remove(curso);
        Material saved = materialRepository.save(material);
        return materialMapper.toResponseDTO(saved);
    }

    private Set<Curso> resolverCursos(List<Integer> idsCursos) {
        if (idsCursos == null || idsCursos.isEmpty()) {
            return Set.of();
        }
        List<Curso> cursosEncontrados = cursoRepository.findAllById(idsCursos);
        if (cursosEncontrados.size() != idsCursos.size()) {
            Set<Integer> idsEncontrados = cursosEncontrados.stream()
                    .map(Curso::getIdCurso)
                    .collect(Collectors.toSet());
            List<Integer> idsFaltantes = idsCursos.stream()
                    .filter(id -> !idsEncontrados.contains(id))
                    .toList();
            throw CursoNoEncontradoException.conIds(idsFaltantes);
        }
        return new java.util.HashSet<>(cursosEncontrados);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaterialResponseDTO> buscarPorTitulo(String titulo, Pageable pageable) {
        return materialRepository.findByTituloContainingIgnoreCase(titulo, pageable)
                .map(materialMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaterialResponseDTO> obtenerPorTipo(String tipo, Pageable pageable) {
        return materialRepository.findByTipo(tipo, pageable)
                .map(materialMapper::toResponseDTO);
    }
}