package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.AulasPorGradoDTO;
import pe.edu.upc.demosm2.dtos.DetalleMatriculaDTO;
import pe.edu.upc.demosm2.dtos.RetencionColegioDTO;
import pe.edu.upc.demosm2.dtos.RetiroPorCursoDTO;
import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.serviceinterfaces.GradoServiceInterface;
import pe.edu.upc.demosm2.serviceinterfaces.ICursoService;
import pe.edu.upc.demosm2.serviceinterfaces.IDetalleMatriculaService;
import pe.edu.upc.demosm2.serviceinterfaces.MatriculaServiceInterface;
import pe.edu.upc.demosm2.serviceinterfaces.PeriodoAcademicoServiceInterface;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/detalles-matricula")
public class DetalleMatriculaController {

    @Autowired
    private IDetalleMatriculaService dmS;

    @Autowired
    private MatriculaServiceInterface mS;

    @Autowired
    private ICursoService cS;

    @Autowired
    private PeriodoAcademicoServiceInterface peS;

    @Autowired
    private GradoServiceInterface gS;

    // ModelMapper en modo STRICT: copia solo los campos con el mismo nombre (idDetalleMatricula, fechaMatricula, estado).
    // DetalleMatricula tiene varios "id..." parecidos (idDetalleMatricula, idMatricula...) y en modo normal los confunde.
    private ModelMapper mapper() {
        ModelMapper m = new ModelMapper();
        m.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return m;
    }

    // Entidad -> DTO: ModelMapper copia los datos y los id de las relaciones se ponen aparte
    private DetalleMatriculaDTO aDTO(ModelMapper m, DetalleMatricula d) {
        DetalleMatriculaDTO dto = m.map(d, DetalleMatriculaDTO.class);
        dto.setIdMatricula(d.getMatricula().getIdMatricula());
        dto.setIdCurso(d.getCurso().getId_curso());
        dto.setIdPeriodo(d.getPeriodo().getIdPeriodo());
        dto.setIdGrado(d.getGrado().getIdGrado());
        return dto;
    }

    // DTO -> entidad: ModelMapper copia los datos y las relaciones se buscan por su id.
    // HU43: la matrícula, el curso, el periodo y el grado deben existir (404 con el dato que falta).
    // HU34: el mismo curso no se registra dos veces en el mismo periodo de una matrícula (409).
    private DetalleMatricula aEntidad(ModelMapper m, DetalleMatriculaDTO dto, Long idActual) {
        if (dto.getIdMatricula() == null || dto.getIdCurso() == null || dto.getIdPeriodo() == null || dto.getIdGrado() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idMatricula, idCurso, idPeriodo e idGrado son obligatorios");
        }
        DetalleMatricula d = m.map(dto, DetalleMatricula.class);
        if (d.getFechaMatricula() == null) d.setFechaMatricula(LocalDate.now());
        d.setEstado(d.getEstado() == null || d.getEstado().isBlank() ? "VIGENTE" : d.getEstado().trim().toUpperCase());
        Matricula matricula = mS.listId(dto.getIdMatricula())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la matrícula: " + dto.getIdMatricula()));
        Curso curso = cS.listId(dto.getIdCurso())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el curso: " + dto.getIdCurso()));
        PeriodoAcademico periodo = peS.listId(dto.getIdPeriodo())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el periodo académico: " + dto.getIdPeriodo()));
        Grado grado = gS.listId(dto.getIdGrado())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el grado: " + dto.getIdGrado()));
        if (dmS.contarDuplicados(dto.getIdMatricula(), dto.getIdPeriodo(), dto.getIdCurso(), idActual == null ? -1L : idActual) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese curso ya está registrado para la matrícula en ese periodo");
        }
        d.setMatricula(matricula);
        d.setCurso(curso);
        d.setPeriodo(periodo);
        d.setGrado(grado);
        return d;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<DetalleMatriculaDTO>> listar() {
        ModelMapper m = mapper();
        List<DetalleMatriculaDTO> lista = dmS.list().stream()
                .map(x -> aDTO(m, x))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> registrar(@RequestBody DetalleMatriculaDTO dto) {
        ModelMapper m = mapper();
        DetalleMatricula d = aEntidad(m, dto, null);
        d.setIdDetalleMatricula(null);
        DetalleMatricula srv = dmS.insert(d);
        return ResponseEntity.status(HttpStatus.CREATED).body(aDTO(m, srv));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        ModelMapper m = mapper();
        Optional<DetalleMatricula> d = dmS.listId(id);
        if (d.isPresent()) {
            return ResponseEntity.ok(aDTO(m, d.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Detalle de matrícula no encontrado");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> actualizar(@RequestBody DetalleMatriculaDTO dto) {
        if (dto.getIdDetalleMatricula() == null || dmS.listId(dto.getIdDetalleMatricula()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Detalle de matrícula no encontrado");
        }
        ModelMapper m = mapper();
        DetalleMatricula d = aEntidad(m, dto, dto.getIdDetalleMatricula());
        dmS.update(d);
        return ResponseEntity.ok(aDTO(m, d));
    }

    // HU43: historial de matrícula de un estudiante (periodo, grado, curso y estado), del más antiguo al más reciente.
    // El personal lo ve de cualquiera; el estudiante, solo el suyo.
    @GetMapping("/historial/{idPersona}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL') or #idPersona.toString() == authentication.name")
    public ResponseEntity<List<DetalleMatriculaDTO>> historialPorPersona(@PathVariable Long idPersona) {
        ModelMapper m = mapper();
        List<DetalleMatriculaDTO> lista = dmS.historialPorPersona(idPersona).stream()
                .map(x -> aDTO(m, x))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        Optional<DetalleMatricula> d = dmS.listId(id);
        if (d.isPresent()) {
            dmS.delete(id);
            return ResponseEntity.ok("Detalle de matrícula eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Detalle de matrícula no encontrado");
        }
    }

    // Query nativo (curso + detalles_matricula) para decidir en qué cursos reforzar: los que más alumnos pierden
    @GetMapping("/reporte-retiro-por-curso")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> reporteRetiroPorCurso() {
        List<Object[]> lista = dmS.reporteRetiroPorCurso();
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No hay detalles de matrícula para generar el reporte.");
        }
        List<RetiroPorCursoDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            RetiroPorCursoDTO dto = new RetiroPorCursoDTO();
            dto.setCurso((String) fila[0]);
            dto.setArea((String) fila[1]);
            dto.setTotalMatriculados(((Number) fila[2]).longValue());
            dto.setRetirados(((Number) fila[3]).longValue());
            dto.setTrasladados(((Number) fila[4]).longValue());
            dto.setPorcentajePerdida(fila[5] == null ? null : ((Number) fila[5]).doubleValue());
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }

    // Query nativo (grados + detalles_matricula) para decidir cuántas aulas abrir por grado (40 alumnos por aula)
    @GetMapping("/reporte-aulas-por-grado/{idPeriodo}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> reporteAulasNecesariasPorGrado(@PathVariable Long idPeriodo) {
        List<Object[]> lista = dmS.reporteAulasNecesariasPorGrado(idPeriodo);
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No hay alumnos vigentes en ese periodo para generar el reporte.");
        }
        List<AulasPorGradoDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            AulasPorGradoDTO dto = new AulasPorGradoDTO();
            dto.setGrado((String) fila[0]);
            dto.setNivel((String) fila[1]);
            dto.setVigentes(((Number) fila[2]).longValue());
            dto.setAulasNecesarias(((Number) fila[3]).longValue());
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }

    // Query nativo (detalles_matricula + matriculas + colegios) para decidir dónde se pierde o se gana población:
    // por colegio, cuántos alumnos siguieron del periodo anterior al actual, cuántos se fueron y cuántos son nuevos
    @GetMapping("/reporte-retencion-colegio/{idPeriodoAnterior}/{idPeriodoActual}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> reporteRetencionPorColegio(@PathVariable Long idPeriodoAnterior, @PathVariable Long idPeriodoActual) {
        List<Object[]> lista = dmS.reporteRetencionPorColegio(idPeriodoAnterior, idPeriodoActual);
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No hay matrículas en esos periodos para generar el reporte.");
        }
        List<RetencionColegioDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            RetencionColegioDTO dto = new RetencionColegioDTO();
            dto.setColegio((String) fila[0]);
            dto.setAlumnosAnterior(((Number) fila[1]).longValue());
            dto.setAlumnosActual(((Number) fila[2]).longValue());
            dto.setSiguieron(((Number) fila[3]).longValue());
            dto.setSeFueron(((Number) fila[4]).longValue());
            dto.setNuevos(((Number) fila[5]).longValue());
            dto.setVariacion(((Number) fila[6]).longValue());
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }
}
