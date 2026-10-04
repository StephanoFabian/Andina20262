package com.andina.plataforma.service;

import com.andina.plataforma.dto.MaterialRequestDTO;
import com.andina.plataforma.dto.MaterialResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de materiales educativos.
 * Define las operaciones CRUD, búsqueda, filtrado y gestión de relaciones con cursos.
 */
public interface MaterialService {

    /**
     * Crea un nuevo material educativo.
     *
     * @param dto datos del material a crear (título, descripción, tipo, URL, cursos asociados, autor)
     * @return el material creado con su ID generado y datos completos
     * @throws PersonaNoEncontradoException si el autor (idPersona) no existe
     * @throws CursoNoEncontradoException si alguno de los cursos asociados no existe
     */
    MaterialResponseDTO crear(MaterialRequestDTO dto);

    /**
     * Obtiene un material por su ID.
     *
     * @param id identificador único del material
     * @return el material con sus datos completos incluyendo cursos asociados
     * @throws MaterialNoEncontradoException si no existe un material con el ID dado
     */
    MaterialResponseDTO obtenerPorId(Integer id);

    /**
     * Lista todos los materiales con paginación.
     *
     * @param pageable información de paginación (página, tamaño, ordenamiento)
     * @return página de materiales con sus datos completos
     */
    Page<MaterialResponseDTO> obtenerTodos(Pageable pageable);

    /**
     * Lista los materiales de una persona específica con paginación.
     *
     * @param idPersona identificador de la persona autora
     * @param pageable información de paginación
     * @return página de materiales de la persona
     */
    Page<MaterialResponseDTO> obtenerPorPersona(Integer idPersona, Pageable pageable);

    /**
     * Actualiza un material existente.
     * Reemplaza completamente la lista de cursos asociados si se proporciona idsCursos.
     *
     * @param id identificador del material a actualizar
     * @param dto nuevos datos del material
     * @return el material actualizado
     * @throws MaterialNoEncontradoException si no existe el material
     * @throws PersonaNoEncontradoException si el nuevo autor no existe
     * @throws CursoNoEncontradoException si alguno de los nuevos cursos no existe
     */
    MaterialResponseDTO actualizar(Integer id, MaterialRequestDTO dto);

    /**
     * Elimina un material por su ID.
     *
     * @param id identificador del material a eliminar
     * @throws MaterialNoEncontradoException si no existe el material
     */
    void eliminar(Integer id);

    /**
     * Agrega un curso a la lista de cursos de un material.
     * Si el curso ya estaba asociado, no hace nada (no duplica).
     *
     * @param idMaterial identificador del material
     * @param idCurso identificador del curso a agregar
     * @return el material actualizado con el curso agregado
     * @throws MaterialNoEncontradoException si no existe el material
     * @throws CursoNoEncontradoException si no existe el curso
     */
    MaterialResponseDTO agregarCursoAMaterial(Integer idMaterial, Integer idCurso);

    /**
     * Quita un curso de la lista de cursos de un material.
     * Si el curso no estaba asociado, no hace nada (no lanza error).
     *
     * @param idMaterial identificador del material
     * @param idCurso identificador del curso a quitar
     * @return el material actualizado con el curso quitado
     * @throws MaterialNoEncontradoException si no existe el material
     * @throws CursoNoEncontradoException si no existe el curso
     */
    MaterialResponseDTO quitarCursoDeMaterial(Integer idMaterial, Integer idCurso);

    /**
     * Busca materiales cuyo título contenga el texto dado (case-insensitive) con paginación.
     *
     * @param titulo texto a buscar en el título
     * @param pageable información de paginación
     * @return página de materiales cuyo título contiene el texto (case-insensitive)
     */
    Page<MaterialResponseDTO> buscarPorTitulo(String titulo, Pageable pageable);

    /**
     * Obtiene materiales filtrados por tipo con paginación.
     *
     * @param tipo tipo de material a filtrar (ej. "PDF", "VIDEO")
     * @param pageable información de paginación
     * @return página de materiales del tipo especificado
     */
    Page<MaterialResponseDTO> obtenerPorTipo(String tipo, Pageable pageable);
}