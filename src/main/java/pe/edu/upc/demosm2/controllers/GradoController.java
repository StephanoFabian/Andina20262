package pe.edu.upc.demosm2.controllers;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<GradoDTOList>> listar() {
        List<GradoDTOList> lista = service.list()
                .stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradoDTOList> buscarId(@PathVariable Long id) {
        Grado grado = service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el grado: " + id));
        return ResponseEntity.ok(toDTO(grado));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
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
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<GradoDTOList> modificar(@PathVariable Long id, @Valid @RequestBody GradoDTOInsert dto) {
        service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el grado: " + id));
        Grado grado = toEntity(dto);
        grado.setIdGrado(id);
        service.update(grado);
        return ResponseEntity.ok(toDTO(grado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
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
    @GetMapping("/pagina")
    public pe.edu.upc.demosm2.dtos.PaginaDTO<pe.edu.upc.demosm2.dtos.GradoDTOList> pagina(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        if(page<0 || page>10000 || size<1 || size>100 || q.length()>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100; búsqueda: hasta 100 caracteres");
        String texto=q.trim().toLowerCase(java.util.Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        java.util.List<pe.edu.upc.demosm2.dtos.GradoDTOList> respuesta=new java.util.ArrayList<>();
        for(Object[] fila:service.pagina(texto,size,page*size)) respuesta.add(filaPagina(fila));
        return new pe.edu.upc.demosm2.dtos.PaginaDTO<>(respuesta,service.contarPagina(texto),page,size);
    }
    private pe.edu.upc.demosm2.dtos.GradoDTOList filaPagina(Object[] fila) {
        pe.edu.upc.demosm2.dtos.GradoDTOList dto=new pe.edu.upc.demosm2.dtos.GradoDTOList();
        dto.setIdGrado((fila[0] == null ? null : ((Number)fila[0]).longValue()));
        dto.setNombre((fila[1] == null ? null : (String)fila[1]));
        dto.setNivel((fila[2] == null ? null : (String)fila[2]));
        return dto;
    }
}
