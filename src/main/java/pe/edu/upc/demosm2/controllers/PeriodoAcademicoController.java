package pe.edu.upc.demosm2.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE','ESTUDIANTE')")
    public ResponseEntity<List<PeriodoAcademicoDTOList>> listar() {
        List<PeriodoAcademicoDTOList> lista = service.list()
                .stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE','ESTUDIANTE')")
    public ResponseEntity<PeriodoAcademicoDTOList> buscarId(@PathVariable Long id) {
        PeriodoAcademico periodo = service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el periodo académico: " + id));
        return ResponseEntity.ok(toDTO(periodo));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<PeriodoAcademicoDTOList> registrar(@Valid @RequestBody PeriodoAcademicoDTOInsert dto) {
        validarPeriodo(dto, null);
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
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<PeriodoAcademicoDTOList> modificar(@PathVariable Long id, @Valid @RequestBody PeriodoAcademicoDTOInsert dto) {
        service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el periodo académico: " + id));
        validarPeriodo(dto, id);
        PeriodoAcademico periodo = toEntity(dto);
        periodo.setIdPeriodo(id);
        service.update(periodo);
        return ResponseEntity.ok(toDTO(periodo));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        PeriodoAcademico periodo = service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el periodo académico: " + id));
        service.delete(periodo.getIdPeriodo());
        return ResponseEntity.noContent().build();
    }

    // HU31: cerrar un periodo (pasa a FINALIZADO); así se puede activar el siguiente
    @PatchMapping("/{id}/cerrar")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<PeriodoAcademicoDTOList> cerrar(@PathVariable Long id) {
        PeriodoAcademico periodo = service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el periodo académico: " + id));
        periodo.setEstado("FINALIZADO");
        service.update(periodo);
        return ResponseEntity.ok(toDTO(periodo));
    }

    // HU31/HU41: inicio antes que fin, sin cruzarse con otro periodo y con un solo periodo ACTIVO a la vez
    private void validarPeriodo(PeriodoAcademicoDTOInsert dto, Long idActual) {
        if (!dto.getFechaInicio().isBefore(dto.getFechaFin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechaInicio debe ser anterior a fechaFin");
        }
        long excluir = idActual == null ? -1L : idActual;
        if (service.contarCruces(dto.getFechaInicio(), dto.getFechaFin(), excluir) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Las fechas se cruzan con otro periodo académico");
        }
        if (dto.getEstado() != null && dto.getEstado().trim().equalsIgnoreCase("ACTIVO") && service.contarActivos(excluir) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya hay un periodo ACTIVO; ciérralo antes de activar otro");
        }
    }

    // Reporte 1 (periodos_academicos + detalles_matricula): matrícula de cada periodo y su variación frente al anterior.
    @GetMapping("/reportes/evolucion-matricula")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
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
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
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
        periodo.setEstado(dto.getEstado() == null ? null : dto.getEstado().trim().toUpperCase());
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
}
