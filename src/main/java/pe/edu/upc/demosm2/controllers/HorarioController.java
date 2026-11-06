package pe.edu.upc.demosm2.controllers;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import pe.edu.upc.demosm2.dtos.HorarioDTO;
import pe.edu.upc.demosm2.serviceinterfaces.IHorarioService;
@RestController
@RequestMapping("/api/horarios")
public class HorarioController {
    private final IHorarioService service;
    public HorarioController(IHorarioService service) { this.service=service; }
    @GetMapping("/{id}")
    public HorarioDTO buscar(@PathVariable Long id) { return service.buscar(id); }
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<HorarioDTO> insertar(@Valid @RequestBody HorarioDTO dto) {
        HorarioDTO creado=service.guardar(null,dto);
        return ResponseEntity.created(URI.create("/api/horarios/" + creado.getIdHorario())).body(creado);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public HorarioDTO actualizar(@PathVariable Long id,@Valid @RequestBody HorarioDTO dto) { return service.guardar(id,dto); }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.eliminar(id); return ResponseEntity.noContent().build(); }
}
