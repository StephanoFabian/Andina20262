package pe.edu.upc.demosm2.controllers;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import pe.edu.upc.demosm2.dtos.IncidenciaDTO;
import pe.edu.upc.demosm2.serviceinterfaces.IIncidenciaService;
@RestController
@RequestMapping("/api/incidencias")
public class IncidenciaController {
    private final IIncidenciaService service;
    public IncidenciaController(IIncidenciaService service) { this.service=service; }
    @GetMapping("/{id}")
    public IncidenciaDTO buscar(@PathVariable Long id) { return service.buscar(id); }
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE')")
    public ResponseEntity<IncidenciaDTO> insertar(@Valid @RequestBody IncidenciaDTO dto) {
        IncidenciaDTO creado=service.guardar(null,dto);
        return ResponseEntity.created(URI.create("/api/incidencias/" + creado.getIdIncidencia())).body(creado);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE')")
    public IncidenciaDTO actualizar(@PathVariable Long id,@Valid @RequestBody IncidenciaDTO dto) { return service.guardar(id,dto); }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.eliminar(id); return ResponseEntity.noContent().build(); }
}
