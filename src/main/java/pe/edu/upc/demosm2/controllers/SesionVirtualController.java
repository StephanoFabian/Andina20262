package pe.edu.upc.demosm2.controllers;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import pe.edu.upc.demosm2.dtos.SesionVirtualDTO;
import pe.edu.upc.demosm2.serviceinterfaces.ISesionVirtualService;
@RestController
@RequestMapping("/api/sesiones-virtuales")
public class SesionVirtualController {
    private final ISesionVirtualService service;
    public SesionVirtualController(ISesionVirtualService service) { this.service=service; }
    @GetMapping("/{id}")
    public SesionVirtualDTO buscar(@PathVariable Long id) { return service.buscar(id); }
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE')")
    public ResponseEntity<SesionVirtualDTO> insertar(@Valid @RequestBody SesionVirtualDTO dto) {
        SesionVirtualDTO creado=service.guardar(null,dto);
        return ResponseEntity.created(URI.create("/api/sesiones-virtuales/" + creado.getIdSesionVirtual())).body(creado);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE')")
    public SesionVirtualDTO actualizar(@PathVariable Long id,@Valid @RequestBody SesionVirtualDTO dto) { return service.guardar(id,dto); }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.eliminar(id); return ResponseEntity.noContent().build(); }
}
