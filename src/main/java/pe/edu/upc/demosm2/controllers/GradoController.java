package pe.edu.upc.demosm2.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.upc.demosm2.dtos.*;
import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.serviceinterfaces.GradoServiceInterface;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/grados")
public class GradoController {
    private final GradoServiceInterface service;

    public GradoController(GradoServiceInterface service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE','ESTUDIANTE')")
    public ResponseEntity<List<GradoDTOList>> listar() {
        List<GradoDTOList> lista = service.list()
                .stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE','ESTUDIANTE')")
    public ResponseEntity<GradoDTOList> buscarId(@PathVariable Long id) {
        Grado grado = service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el grado: " + id));
        return ResponseEntity.ok(toDTO(grado));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<GradoDTOList> registrar(@Valid @RequestBody GradoDTOInsert dto) {
        Grado grado = toEntity(dto);
        grado.setIdGrado(null);
        service.insert(grado);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(grado.getIdGrado())
                .toUri();
        return ResponseEntity.created(location).body(toDTO(grado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<GradoDTOList> modificar(@PathVariable Long id, @Valid @RequestBody GradoDTOInsert dto) {
        service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el grado: " + id));
        Grado grado = toEntity(dto);
        grado.setIdGrado(id);
        service.update(grado);
        return ResponseEntity.ok(toDTO(grado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Grado grado = service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el grado: " + id));
        service.delete(grado.getIdGrado());
        return ResponseEntity.noContent().build();
    }

    private GradoDTOList toDTO(Grado grado) {
        GradoDTOList dto = new GradoDTOList();
        dto.setIdGrado(grado.getIdGrado());
        dto.setNombre(grado.getNombre());
        dto.setNivel(grado.getNivel());
        return dto;
    }

    private Grado toEntity(GradoDTOInsert dto) {
        Grado grado = new Grado();
        grado.setNombre(dto.getNombre());
        grado.setNivel(dto.getNivel());
        return grado;
    }
}
