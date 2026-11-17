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
    @GetMapping("/pagina")
    public pe.edu.upc.demosm2.dtos.PaginaDTO<pe.edu.upc.demosm2.dtos.SesionVirtualDTO> pagina(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        if(page<0 || page>10000 || size<1 || size>100 || q.length()>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100; búsqueda: hasta 100 caracteres");
        String texto=q.trim().toLowerCase(java.util.Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        java.util.List<pe.edu.upc.demosm2.dtos.SesionVirtualDTO> respuesta=new java.util.ArrayList<>();
        for(Object[] fila:service.pagina(texto,size,page*size)) respuesta.add(filaPagina(fila));
        return new pe.edu.upc.demosm2.dtos.PaginaDTO<>(respuesta,service.contarPagina(texto),page,size);
    }
    private pe.edu.upc.demosm2.dtos.SesionVirtualDTO filaPagina(Object[] fila) {
        pe.edu.upc.demosm2.dtos.SesionVirtualDTO dto=new pe.edu.upc.demosm2.dtos.SesionVirtualDTO();
        dto.setIdSesionVirtual((fila[0] == null ? null : ((Number)fila[0]).longValue()));
        dto.setTitulo((fila[1] == null ? null : (String)fila[1]));
        dto.setFecha((fila[2] == null ? null : (fila[2] instanceof java.time.LocalDate ? (java.time.LocalDate)fila[2] : ((java.sql.Date)fila[2]).toLocalDate())));
        dto.setHoraInicio((fila[3] == null ? null : (fila[3] instanceof java.time.LocalTime ? (java.time.LocalTime)fila[3] : ((java.sql.Time)fila[3]).toLocalTime())));
        dto.setHoraFin((fila[4] == null ? null : (fila[4] instanceof java.time.LocalTime ? (java.time.LocalTime)fila[4] : ((java.sql.Time)fila[4]).toLocalTime())));
        dto.setEnlace((fila[5] == null ? null : (String)fila[5]));
        dto.setEstado((fila[6] == null ? null : (String)fila[6]));
        dto.setIdAsignacion((fila[7] == null ? null : ((Number)fila[7]).longValue()));
        return dto;
    }
}
