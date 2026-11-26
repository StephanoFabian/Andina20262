package pe.edu.upc.demosm2.controllers;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import pe.edu.upc.demosm2.dtos.ConvenioDTO;
import pe.edu.upc.demosm2.serviceinterfaces.IConvenioService;
@RestController
@RequestMapping("/api/convenios")
public class ConvenioController {
    private final IConvenioService service;
    public ConvenioController(IConvenioService service) { this.service=service; }
    @GetMapping("/{id}")
    public ConvenioDTO buscar(@PathVariable Long id) { return service.buscar(id); }
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<ConvenioDTO> insertar(@Valid @RequestBody ConvenioDTO dto) {
        ConvenioDTO creado=service.guardar(null,dto);
        return ResponseEntity.created(URI.create("/api/convenios/" + creado.getIdConvenio())).body(creado);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ConvenioDTO actualizar(@PathVariable Long id,@Valid @RequestBody ConvenioDTO dto) { return service.guardar(id,dto); }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.eliminar(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/pagina")
    public pe.edu.upc.demosm2.dtos.PaginaDTO<pe.edu.upc.demosm2.dtos.ConvenioDTO> pagina(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        if(page<0 || page>10000 || size<1 || size>100 || q.length()>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100; búsqueda: hasta 100 caracteres");
        String texto=q.trim().toLowerCase(java.util.Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        java.util.List<pe.edu.upc.demosm2.dtos.ConvenioDTO> respuesta=new java.util.ArrayList<>();
        for(Object[] fila:service.pagina(texto,size,page*size)) respuesta.add(filaPagina(fila));
        return new pe.edu.upc.demosm2.dtos.PaginaDTO<>(respuesta,service.contarPagina(texto),page,size);
    }
    private pe.edu.upc.demosm2.dtos.ConvenioDTO filaPagina(Object[] fila) {
        pe.edu.upc.demosm2.dtos.ConvenioDTO dto=new pe.edu.upc.demosm2.dtos.ConvenioDTO();
        dto.setIdConvenio((fila[0] == null ? null : ((Number)fila[0]).longValue()));
        dto.setNombre((fila[1] == null ? null : (String)fila[1]));
        dto.setInstitucion((fila[2] == null ? null : (String)fila[2]));
        dto.setEntidad((fila[3] == null ? null : (String)fila[3]));
        dto.setTipoEntidad((fila[4] == null ? null : (String)fila[4]));
        dto.setRegion((fila[5] == null ? null : (String)fila[5]));
        dto.setPresupuesto((fila[6] == null ? null : (java.math.BigDecimal)fila[6]));
        dto.setFechaInicio((fila[7] == null ? null : (fila[7] instanceof java.time.LocalDate ? (java.time.LocalDate)fila[7] : ((java.sql.Date)fila[7]).toLocalDate())));
        dto.setFechaFin((fila[8] == null ? null : (fila[8] instanceof java.time.LocalDate ? (java.time.LocalDate)fila[8] : ((java.sql.Date)fila[8]).toLocalDate())));
        dto.setEstado((fila[9] == null ? null : (String)fila[9]));
        dto.setIdColegio((fila[10] == null ? null : ((Number)fila[10]).longValue()));
        return dto;
    }
    @GetMapping("/por-vencer/pagina")
    public pe.edu.upc.demosm2.dtos.PaginaDTO<pe.edu.upc.demosm2.dtos.ConvenioPorVencerDTO> vencimientosPagina(@RequestParam(defaultValue="90") int dias,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size) {
        if(page<0 || page>10000 || size<1 || size>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100");
        if(dias<0 || dias>3650)throw new IllegalArgumentException("Plazo permitido: 0 a 3650 días");
        java.util.List<pe.edu.upc.demosm2.dtos.ConvenioPorVencerDTO> resultado=new java.util.ArrayList<>();
        for(Object[] fila:service.vencimientosPagina(dias,size,page*size)) {
            pe.edu.upc.demosm2.dtos.ConvenioPorVencerDTO dto=new pe.edu.upc.demosm2.dtos.ConvenioPorVencerDTO();dto.setIdConvenio(((Number)fila[0]).longValue());dto.setNombre((String)fila[1]);dto.setEntidad((String)fila[2]);dto.setRegion((String)fila[3]);dto.setFechaFin(fila[4] instanceof java.time.LocalDate ? (java.time.LocalDate)fila[4]:((java.sql.Date)fila[4]).toLocalDate());dto.setPresupuesto((java.math.BigDecimal)fila[5]);dto.setEstado((String)fila[6]);dto.setDiasRestantes(((Number)fila[7]).longValue());dto.setSituacion(dto.getDiasRestantes()<0?"Vencido":"Por vencer");resultado.add(dto);
        }
        return new pe.edu.upc.demosm2.dtos.PaginaDTO<>(resultado,service.contarVencimientos(dias),page,size);
    }
}
