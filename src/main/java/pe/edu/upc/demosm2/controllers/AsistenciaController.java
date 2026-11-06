package pe.edu.upc.demosm2.controllers;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import pe.edu.upc.demosm2.dtos.AsistenciaDTO;
import pe.edu.upc.demosm2.serviceinterfaces.IAsistenciaService;
@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {
    private final IAsistenciaService service;
    public AsistenciaController(IAsistenciaService service) { this.service=service; }
    @GetMapping("/{id}")
    public AsistenciaDTO buscar(@PathVariable Long id) { return service.buscar(id); }
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE')")
    public ResponseEntity<AsistenciaDTO> insertar(@Valid @RequestBody AsistenciaDTO dto) {
        AsistenciaDTO creado=service.guardar(null,dto);
        return ResponseEntity.created(URI.create("/api/asistencias/" + creado.getIdAsistencia())).body(creado);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE')")
    public AsistenciaDTO actualizar(@PathVariable Long id,@Valid @RequestBody AsistenciaDTO dto) { return service.guardar(id,dto); }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.eliminar(id); return ResponseEntity.noContent().build(); }
}
