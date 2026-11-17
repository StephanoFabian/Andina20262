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
    @GetMapping("/pagina")
    public pe.edu.upc.demosm2.dtos.PaginaDTO<pe.edu.upc.demosm2.dtos.AsistenciaDTO> pagina(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        if(page<0 || page>10000 || size<1 || size>100 || q.length()>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100; búsqueda: hasta 100 caracteres");
        String texto=q.trim().toLowerCase(java.util.Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        java.util.List<pe.edu.upc.demosm2.dtos.AsistenciaDTO> respuesta=new java.util.ArrayList<>();
        for(Object[] fila:service.pagina(texto,size,page*size)) respuesta.add(filaPagina(fila));
        return new pe.edu.upc.demosm2.dtos.PaginaDTO<>(respuesta,service.contarPagina(texto),page,size);
    }
    private pe.edu.upc.demosm2.dtos.AsistenciaDTO filaPagina(Object[] fila) {
        pe.edu.upc.demosm2.dtos.AsistenciaDTO dto=new pe.edu.upc.demosm2.dtos.AsistenciaDTO();
        dto.setIdAsistencia((fila[0] == null ? null : ((Number)fila[0]).longValue()));
        dto.setEstado((fila[1] == null ? null : (String)fila[1]));
        dto.setObservacion((fila[2] == null ? null : (String)fila[2]));
        dto.setIdSesionVirtual((fila[3] == null ? null : ((Number)fila[3]).longValue()));
        dto.setIdPersona((fila[4] == null ? null : ((Number)fila[4]).longValue()));
        return dto;
    }
}
