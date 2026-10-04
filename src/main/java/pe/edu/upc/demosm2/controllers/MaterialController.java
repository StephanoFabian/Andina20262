package com.andina.plataforma.controller;

import com.andina.plataforma.dto.MaterialRequestDTO;
import com.andina.plataforma.dto.MaterialResponseDTO;
import com.andina.plataforma.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de materiales educativos.
 * Proporciona endpoints para CRUD completo, paginación, búsqueda por título y filtrado por tipo.
 */
@RestController
@RequestMapping("/api/materiales")
@RequiredArgsConstructor
@Tag(name = "Material", description = "Gestión de materiales educativos")
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping
    @Operation(summary = "Crear nuevo material")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Material creado exitosamente",
            content = @Content(schema = @Schema(implementation = MaterialResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Persona o cursos no encontrados")
    })
    public ResponseEntity<MaterialResponseDTO> crear(
            @Valid @RequestBody MaterialRequestDTO requestDTO) {
        MaterialResponseDTO response = materialService.crear(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos los materiales con paginación")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Página de materiales",
            content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    })
    public ResponseEntity<Page<MaterialResponseDTO>> obtenerTodos(
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(materialService.obtenerTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener material por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Material encontrado",
            content = @Content(schema = @Schema(implementation = MaterialResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Material no encontrado")
    })
    public ResponseEntity<MaterialResponseDTO> obtenerPorId(
            @Parameter(description = "ID del material") @PathVariable Integer id) {
        return ResponseEntity.ok(materialService.obtenerPorId(id));
    }

    @GetMapping("/persona/{idPersona}")
    @Operation(summary = "Obtener materiales por persona con paginación")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Página de materiales de la persona"),
        @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    })
    public ResponseEntity<Page<MaterialResponseDTO>> obtenerPorPersona(
            @Parameter(description = "ID de la persona") @PathVariable Integer idPersona,
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(materialService.obtenerPorPersona(idPersona, pageable));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar materiales por título (case-insensitive) con paginación")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Página de materiales que coinciden"),
        @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    })
    public ResponseEntity<Page<MaterialResponseDTO>> buscarPorTitulo(
            @Parameter(description = "Texto a buscar en el título") @RequestParam String titulo,
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(materialService.buscarPorTitulo(titulo, pageable));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Obtener materiales por tipo con paginación")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Página de materiales del tipo especificado"),
        @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    })
    public ResponseEntity<Page<MaterialResponseDTO>> obtenerPorTipo(
            @Parameter(description = "Tipo de material (ej. PDF, VIDEO)") @PathVariable String tipo,
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(materialService.obtenerPorTipo(tipo, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar material")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Material actualizado",
            content = @Content(schema = @Schema(implementation = MaterialResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Material, persona o cursos no encontrados")
    })
    public ResponseEntity<MaterialResponseDTO> actualizar(
            @Parameter(description = "ID del material") @PathVariable Integer id,
            @Valid @RequestBody MaterialRequestDTO requestDTO) {
        return ResponseEntity.ok(materialService.actualizar(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar material")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Material eliminado"),
        @ApiResponse(responseCode = "404", description = "Material no encontrado")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del material") @PathVariable Integer id) {
        materialService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cursos/{idCurso}")
    @Operation(summary = "Agregar curso a material")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Curso agregado al material",
            content = @Content(schema = @Schema(implementation = MaterialResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Material o curso no encontrado")
    })
    public ResponseEntity<MaterialResponseDTO> agregarCurso(
            @Parameter(description = "ID del material") @PathVariable Integer id,
            @Parameter(description = "ID del curso") @PathVariable Integer idCurso) {
        return ResponseEntity.ok(materialService.agregarCursoAMaterial(id, idCurso));
    }

    @DeleteMapping("/{id}/cursos/{idCurso}")
    @Operation(summary = "Quitar curso de material")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Curso quitado del material",
            content = @Content(schema = @Schema(implementation = MaterialResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Material o curso no encontrado")
    })
    public ResponseEntity<MaterialResponseDTO> quitarCurso(
            @Parameter(description = "ID del material") @PathVariable Integer id,
            @Parameter(description = "ID del curso") @PathVariable Integer idCurso) {
        return ResponseEntity.ok(materialService.quitarCursoDeMaterial(id, idCurso));
    }
}