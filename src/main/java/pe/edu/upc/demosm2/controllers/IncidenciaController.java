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
    @GetMapping("/pagina")
    public pe.edu.upc.demosm2.dtos.PaginaDTO<pe.edu.upc.demosm2.dtos.IncidenciaDTO> pagina(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        if(page<0 || page>10000 || size<1 || size>100 || q.length()>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100; búsqueda: hasta 100 caracteres");
        String texto=q.trim().toLowerCase(java.util.Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        java.util.List<pe.edu.upc.demosm2.dtos.IncidenciaDTO> respuesta=new java.util.ArrayList<>();
        for(Object[] fila:service.pagina(texto,size,page*size)) respuesta.add(filaPagina(fila));
        return new pe.edu.upc.demosm2.dtos.PaginaDTO<>(respuesta,service.contarPagina(texto),page,size);
    }
    private pe.edu.upc.demosm2.dtos.IncidenciaDTO filaPagina(Object[] fila) {
        pe.edu.upc.demosm2.dtos.IncidenciaDTO dto=new pe.edu.upc.demosm2.dtos.IncidenciaDTO();
        dto.setIdIncidencia((fila[0] == null ? null : ((Number)fila[0]).longValue()));
        dto.setTitulo((fila[1] == null ? null : (String)fila[1]));
        dto.setDescripcion((fila[2] == null ? null : (String)fila[2]));
        dto.setFecha((fila[3] == null ? null : (fila[3] instanceof java.time.LocalDate ? (java.time.LocalDate)fila[3] : ((java.sql.Date)fila[3]).toLocalDate())));
        dto.setEstado((fila[4] == null ? null : (String)fila[4]));
        dto.setPrioridad((fila[5] == null ? null : (String)fila[5]));
        dto.setIdPersona((fila[6] == null ? null : ((Number)fila[6]).longValue()));
        dto.setIdColegio((fila[7] == null ? null : ((Number)fila[7]).longValue()));
        return dto;
    }
}
