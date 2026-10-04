package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.AulasPorGradoDTO;
import pe.edu.upc.demosm2.dtos.DetalleMatriculaDTO;
import pe.edu.upc.demosm2.dtos.RetiroPorCursoDTO;
import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.servicesinterfaces.IDetalleMatriculaService;
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

    @GetMapping
    public ResponseEntity<List<DetalleMatriculaDTO>> listar() {
        List<DetalleMatriculaDTO> lista = dmS.list().stream()
                .map(this::aDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> registrar(@RequestBody DetalleMatriculaDTO dto) {
        try {
            DetalleMatricula d = aEntidad(dto);
            d.setIdDetalleMatricula(null);
            DetalleMatricula srv = dmS.insert(d);
            return ResponseEntity.status(HttpStatus.CREATED).body(aDTO(srv));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: la matrícula, el curso, el periodo o el grado no existe en la base de datos.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<DetalleMatricula> d = dmS.listId(id);
        if (d.isPresent()) {
            return ResponseEntity.ok(aDTO(d.get()));
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
            DetalleMatricula d = aEntidad(dto);
            dmS.update(d);
            return ResponseEntity.ok(aDTO(d));
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

    @GetMapping("/alumno/{idPersona}")
    public ResponseEntity<List<DetalleMatriculaDTO>> listarPorAlumno(@PathVariable Long idPersona) {
        List<DetalleMatriculaDTO> lista = dmS.listarDetallesPorAlumno(idPersona).stream()
                .map(this::aDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // Reporte (curso + detalles_matricula): cursos que más alumnos pierden (retirados + trasladados)
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
            dto.setPorcentajePerdida(((Number) fila[5]).doubleValue());
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }

    // Reporte (grados + detalles_matricula): alumnos vigentes por grado en un periodo y aulas necesarias (40 por aula)
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

    // Las relaciones se arman con su id (como Rol en UsuarioController); si alguna no existe salta DataIntegrityViolation
    private DetalleMatricula aEntidad(DetalleMatriculaDTO dto) {
        DetalleMatricula d = new DetalleMatricula();
        d.setIdDetalleMatricula(dto.getIdDetalleMatricula());
        d.setFechaMatricula(dto.getFechaMatricula() != null ? dto.getFechaMatricula() : LocalDate.now());
        d.setEstado(dto.getEstado() != null ? dto.getEstado() : "VIGENTE");
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

    private DetalleMatriculaDTO aDTO(DetalleMatricula d) {
        DetalleMatriculaDTO dto = new DetalleMatriculaDTO();
        dto.setIdDetalleMatricula(d.getIdDetalleMatricula());
        dto.setFechaMatricula(d.getFechaMatricula());
        dto.setEstado(d.getEstado());
        dto.setIdMatricula(d.getMatricula().getIdMatricula());
        dto.setIdCurso(d.getCurso().getId_curso());
        dto.setIdPeriodo(d.getPeriodo().getIdPeriodo());
        dto.setIdGrado(d.getGrado().getIdGrado());
        return dto;
    }
}
