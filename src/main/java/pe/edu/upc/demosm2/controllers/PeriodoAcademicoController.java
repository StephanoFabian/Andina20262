package pe.edu.upc.demosm2.controllers;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.upc.demosm2.dtos.*;
import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.serviceinterfaces.PeriodoAcademicoServiceInterface;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/periodos-academicos")
public class PeriodoAcademicoController {
    private final PeriodoAcademicoServiceInterface service;

    public PeriodoAcademicoController(PeriodoAcademicoServiceInterface service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PeriodoAcademicoDTOList>> listar() {
        List<PeriodoAcademicoDTOList> lista = service.list()
                .stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeriodoAcademicoDTOList> buscarId(@PathVariable Long id) {
        PeriodoAcademico periodo = service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el periodo académico: " + id));
        return ResponseEntity.ok(toDTO(periodo));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<PeriodoAcademicoDTOList> registrar(@Valid @RequestBody PeriodoAcademicoDTOInsert dto) {
        PeriodoAcademico periodo = toEntity(dto);
        periodo.setIdPeriodo(null);
        service.insert(periodo);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(periodo.getIdPeriodo())
                .toUri();
        return ResponseEntity.created(location).body(toDTO(periodo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<PeriodoAcademicoDTOList> modificar(@PathVariable Long id, @Valid @RequestBody PeriodoAcademicoDTOInsert dto) {
        service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el periodo académico: " + id));
        PeriodoAcademico periodo = toEntity(dto);
        periodo.setIdPeriodo(id);
        service.update(periodo);
        return ResponseEntity.ok(toDTO(periodo));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        PeriodoAcademico periodo = service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el periodo académico: " + id));
        service.delete(periodo.getIdPeriodo());
        return ResponseEntity.noContent().build();
    }

    // Reporte 1 (periodos_academicos + detalles_matricula): matrícula de cada periodo y su variación frente al anterior.
    @GetMapping("/reportes/evolucion-matricula")
    public ResponseEntity<List<EvolucionPeriodoDTO>> evolucionDeMatricula() {
        List<EvolucionPeriodoDTO> lista = service.evolucionDeMatricula()
                .stream()
                .map(fila -> {
                    EvolucionPeriodoDTO dto = new EvolucionPeriodoDTO();
                    dto.setPeriodo((String) fila[0]);
                    dto.setFechaInicio(aFecha(fila[1]));
                    dto.setEstado((String) fila[2]);
                    dto.setMatriculados(aLong(fila[3]));
                    dto.setPeriodoAnterior(aLong(fila[4]));
                    dto.setVariacionPorcentual(aDouble(fila[5]));
                    return dto;
                })
                .toList();
        return ResponseEntity.ok(lista);
    }

    // Reporte 2 (periodos_academicos + detalles_matricula): matrículas anticipadas, tardías y fuera del periodo.
    @GetMapping("/reportes/matricula-tardia")
    public ResponseEntity<List<MatriculaTardiaDTO>> matriculaTardiaPorPeriodo() {
        List<MatriculaTardiaDTO> lista = service.matriculaTardiaPorPeriodo()
                .stream()
                .map(fila -> {
                    MatriculaTardiaDTO dto = new MatriculaTardiaDTO();
                    dto.setPeriodo((String) fila[0]);
                    dto.setFechaInicio(aFecha(fila[1]));
                    dto.setTotalMatriculas(aLong(fila[2]));
                    dto.setAnticipadas(aLong(fila[3]));
                    dto.setTardias(aLong(fila[4]));
                    dto.setFueraDelPeriodo(aLong(fila[5]));
                    dto.setPorcentajeTardias(aDouble(fila[6]));
                    return dto;
                })
                .toList();
        return ResponseEntity.ok(lista);
    }

    private PeriodoAcademicoDTOList toDTO(PeriodoAcademico periodo) {
        PeriodoAcademicoDTOList dto = new PeriodoAcademicoDTOList();
        dto.setIdPeriodo(periodo.getIdPeriodo());
        dto.setNombre(periodo.getNombre());
        dto.setFechaInicio(periodo.getFechaInicio());
        dto.setFechaFin(periodo.getFechaFin());
        dto.setEstado(periodo.getEstado());
        return dto;
    }

    private PeriodoAcademico toEntity(PeriodoAcademicoDTOInsert dto) {
        PeriodoAcademico periodo = new PeriodoAcademico();
        periodo.setNombre(dto.getNombre());
        periodo.setFechaInicio(dto.getFechaInicio());
        periodo.setFechaFin(dto.getFechaFin());
        periodo.setEstado(dto.getEstado());
        return periodo;
    }

    private static Long aLong(Object valor) {
        return valor == null ? null : ((Number) valor).longValue();
    }

    private static Double aDouble(Object valor) {
        return valor == null ? null : ((Number) valor).doubleValue();
    }

    private static LocalDate aFecha(Object valor) {
        if (valor == null) return null;
        if (valor instanceof LocalDate f) return f;
        return ((java.sql.Date) valor).toLocalDate();
    }
    @GetMapping("/pagina")
    public pe.edu.upc.demosm2.dtos.PaginaDTO<pe.edu.upc.demosm2.dtos.PeriodoAcademicoDTOList> pagina(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        if(page<0 || page>10000 || size<1 || size>100 || q.length()>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100; búsqueda: hasta 100 caracteres");
        String texto=q.trim().toLowerCase(java.util.Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        java.util.List<pe.edu.upc.demosm2.dtos.PeriodoAcademicoDTOList> respuesta=new java.util.ArrayList<>();
        for(Object[] fila:service.pagina(texto,size,page*size)) respuesta.add(filaPagina(fila));
        return new pe.edu.upc.demosm2.dtos.PaginaDTO<>(respuesta,service.contarPagina(texto),page,size);
    }
    private pe.edu.upc.demosm2.dtos.PeriodoAcademicoDTOList filaPagina(Object[] fila) {
        pe.edu.upc.demosm2.dtos.PeriodoAcademicoDTOList dto=new pe.edu.upc.demosm2.dtos.PeriodoAcademicoDTOList();
        dto.setIdPeriodo((fila[0] == null ? null : ((Number)fila[0]).longValue()));
        dto.setNombre((fila[1] == null ? null : (String)fila[1]));
        dto.setFechaInicio((fila[2] == null ? null : (fila[2] instanceof java.time.LocalDate ? (java.time.LocalDate)fila[2] : ((java.sql.Date)fila[2]).toLocalDate())));
        dto.setFechaFin((fila[3] == null ? null : (fila[3] instanceof java.time.LocalDate ? (java.time.LocalDate)fila[3] : ((java.sql.Date)fila[3]).toLocalDate())));
        dto.setEstado((fila[4] == null ? null : (String)fila[4]));
        return dto;
    }
}
