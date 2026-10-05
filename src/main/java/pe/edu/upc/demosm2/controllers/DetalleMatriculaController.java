package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.AulasPorGradoDTO;
import pe.edu.upc.demosm2.dtos.DetalleMatriculaDTO;
import pe.edu.upc.demosm2.dtos.RetencionColegioDTO;
import pe.edu.upc.demosm2.dtos.RetiroPorCursoDTO;
import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.servicesinterfaces.IDetalleMatriculaService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    // DTO -> entidad: ModelMapper copia los datos y las relaciones se arman con su id (como Rol en UsuarioController)
    private DetalleMatricula aEntidad(ModelMapper m, DetalleMatriculaDTO dto) {
        DetalleMatricula d = m.map(dto, DetalleMatricula.class);
        if (d.getFechaMatricula() == null) d.setFechaMatricula(LocalDate.now());
        if (d.getEstado() == null) d.setEstado("VIGENTE");
        Matricula matricula = new Matricula();
        matricula.setIdMatricula(dto.getIdMatricula());
        d.setMatricula(matricula);
        Curso curso = new Curso();
        curso.setId_curso(dto.getIdCurso());
        d.setCurso(curso);
        PeriodoAcademico periodo = new PeriodoAcademico();
        periodo.setIdPeriodo(dto.getIdPeriodo());
        d.setPeriodo(periodo);
        Grado grado = new Grado();
        grado.setIdGrado(dto.getIdGrado());
        d.setGrado(grado);
        return d;
    }

    @GetMapping
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
        try {
            ModelMapper m = mapper();
            DetalleMatricula d = aEntidad(m, dto);
            d.setIdDetalleMatricula(null);
            DetalleMatricula srv = dmS.insert(d);
            return ResponseEntity.status(HttpStatus.CREATED).body(aDTO(m, srv));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: la matrícula, el curso, el periodo o el grado no existe en la base de datos.");
        }
    }

    @GetMapping("/{id}")
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
        try {
            ModelMapper m = mapper();
            DetalleMatricula d = aEntidad(m, dto);
            dmS.update(d);
            return ResponseEntity.ok(aDTO(m, d));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: la matrícula, el curso, el periodo o el grado no existe en la base de datos.");
        }
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
