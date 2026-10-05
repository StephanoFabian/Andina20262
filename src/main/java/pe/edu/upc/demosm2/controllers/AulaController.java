package pe.edu.upc.demosm2.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.upc.demosm2.dtos.*;
import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.serviceinterfaces.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/aula")
public class AulaController {
    private final AulaServiceInterface service;
    private final ColegioServiceInterface colegios;

    public AulaController(AulaServiceInterface service, ColegioServiceInterface colegios) {
        this.service = service;
        this.colegios = colegios;
    }

    @GetMapping
    @Operation(summary = "Listar aulas, opcionalmente por colegio", description = "Arreglo paginado por ID; X-Has-Next indica si hay más resultados. size: 1 a 100.")
    public ResponseEntity<List<AulaDTOList>> listar(
            @RequestParam(required = false) @Positive Long idColegio,
            @RequestParam(defaultValue = "0") @Min(0) @Max(10000) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        if (idColegio != null) findColegio(idColegio);
        Slice<AulaDTOList> result = service.list(idColegio, PageRequest.of(page, size, Sort.by("idAula")))
                .map(this::toDto);
        return ResponseEntity.ok().header("X-Has-Next", String.valueOf(result.hasNext()))
                .header("X-Page", String.valueOf(page)).header("X-Page-Size", String.valueOf(size))
                .body(result.getContent());
    }

    @GetMapping("/{id}")
    public AulaDTOList buscar(@PathVariable @Positive Long id) { return toDto(find(id)); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<AulaDTOList> registrar(@Valid @RequestBody AulaDTOInsert dto) {
        if (dto.getIdAula() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID se genera al crear el aula");
        Aula aula = new Aula();
        apply(dto, aula);
        service.insert(aula);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(aula.getIdAula()).toUri();
        return ResponseEntity.created(location).body(toDto(aula));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public AulaDTOList actualizar(@PathVariable @Positive Long id, @Valid @RequestBody AulaDTOInsert dto) {
        if (dto.getIdAula() != null && !id.equals(dto.getIdAula()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID del cuerpo debe coincidir con la ruta");
        Aula aula = find(id);
        apply(dto, aula);
        service.insert(aula);
        return toDto(aula);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        service.delete(find(id).getIdAula());
        return ResponseEntity.noContent().build();
    }

    private void apply(AulaDTOInsert dto, Aula aula) {
        aula.setNombre(dto.getNombre());
        aula.setSeccion(dto.getSeccion());
        aula.setCapacidad(dto.getCapacidad());
        aula.setColegio(findColegio(dto.getIdColegio()));
    }

    private Aula find(Long id) {
        return service.listId(id).orElseThrow(() -> new ResourceNotFoundException("No existe el aula: " + id));
    }

    private Colegio findColegio(Long id) {
        return colegios.listId(id).orElseThrow(() -> new ResourceNotFoundException("No existe el colegio: " + id));
    }

    private AulaDTOList toDto(Aula aula) {
        AulaDTOList dto = new AulaDTOList();
        dto.setIdAula(aula.getIdAula());
        dto.setNombre(aula.getNombre());
        dto.setSeccion(aula.getSeccion());
        dto.setCapacidad(aula.getCapacidad());
        dto.setIdColegio(aula.getColegio().getIdColegio());
        return dto;
    }
}
