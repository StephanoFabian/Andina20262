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
    @GetMapping("/pagina")
    public pe.edu.upc.demosm2.dtos.PaginaDTO<pe.edu.upc.demosm2.dtos.HorarioDTO> pagina(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        if(page<0 || page>10000 || size<1 || size>100 || q.length()>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100; búsqueda: hasta 100 caracteres");
        String texto=q.trim().toLowerCase(java.util.Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        java.util.List<pe.edu.upc.demosm2.dtos.HorarioDTO> respuesta=new java.util.ArrayList<>();
        for(Object[] fila:service.pagina(texto,size,page*size)) respuesta.add(filaPagina(fila));
        return new pe.edu.upc.demosm2.dtos.PaginaDTO<>(respuesta,service.contarPagina(texto),page,size);
    }
    private pe.edu.upc.demosm2.dtos.HorarioDTO filaPagina(Object[] fila) {
        pe.edu.upc.demosm2.dtos.HorarioDTO dto=new pe.edu.upc.demosm2.dtos.HorarioDTO();
        dto.setIdHorario((fila[0] == null ? null : ((Number)fila[0]).longValue()));
        dto.setDiaSemana((fila[1] == null ? null : ((Number)fila[1]).intValue()));
        dto.setHoraInicio((fila[2] == null ? null : (fila[2] instanceof java.time.LocalTime ? (java.time.LocalTime)fila[2] : ((java.sql.Time)fila[2]).toLocalTime())));
        dto.setHoraFin((fila[3] == null ? null : (fila[3] instanceof java.time.LocalTime ? (java.time.LocalTime)fila[3] : ((java.sql.Time)fila[3]).toLocalTime())));
        dto.setIdAsignacion((fila[4] == null ? null : ((Number)fila[4]).longValue()));
        return dto;
    }
}
