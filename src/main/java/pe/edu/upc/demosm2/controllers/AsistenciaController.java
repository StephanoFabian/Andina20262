package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.AsistenciaDTO;
import pe.edu.upc.demosm2.dtos.ParticipacionEstudianteDTO;
import pe.edu.upc.demosm2.entities.Asistencia;
import pe.edu.upc.demosm2.entities.Curso;
import pe.edu.upc.demosm2.entities.PeriodoAcademico;
import pe.edu.upc.demosm2.entities.Persona;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.serviceinterfaces.IAsistenciaService;
import pe.edu.upc.demosm2.serviceinterfaces.ICursoService;
import pe.edu.upc.demosm2.serviceinterfaces.IPersonaService;
import pe.edu.upc.demosm2.serviceinterfaces.PeriodoAcademicoServiceInterface;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// HU08: asistencia y calificaciones por alumno y sesión, y reporte de participación para detectar deserción a tiempo
@RestController
@RequestMapping("/asistencias")
public class AsistenciaController {
    @Autowired
    private IAsistenciaService aS;

    @Autowired
    private IPersonaService pS;

    @Autowired
    private ICursoService cS;

    @Autowired
    private PeriodoAcademicoServiceInterface peS;

    // ModelMapper en modo STRICT: copia solo los campos con el mismo nombre; las relaciones se ponen aparte
    private ModelMapper mapper() {
        ModelMapper m = new ModelMapper();
        m.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return m;
    }

    private AsistenciaDTO aDTO(ModelMapper m, Asistencia a) {
        AsistenciaDTO dto = m.map(a, AsistenciaDTO.class);
        dto.setIdPersona(a.getPersona().getIdPersona());
        dto.setIdCurso(a.getCurso().getId_curso());
        dto.setIdPeriodo(a.getPeriodo().getIdPeriodo());
        return dto;
    }

    // La persona debe ser ESTUDIANTE, el curso y el periodo deben existir y la sesión debe caer dentro del periodo
    private Asistencia aEntidad(ModelMapper m, AsistenciaDTO dto) {
        Persona persona = pS.listId(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la persona: " + dto.getIdPersona()));
        if (!pS.tieneAlgunRol(persona, "ESTUDIANTE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La asistencia se registra solo para personas con rol ESTUDIANTE");
        }
        Curso curso = cS.listId(dto.getIdCurso())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el curso: " + dto.getIdCurso()));
        PeriodoAcademico periodo = peS.listId(dto.getIdPeriodo())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el periodo académico: " + dto.getIdPeriodo()));
        if (dto.getFechaSesion().isBefore(periodo.getFechaInicio()) || dto.getFechaSesion().isAfter(periodo.getFechaFin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de la sesión está fuera del periodo académico");
        }
        Asistencia a = m.map(dto, Asistencia.class);
        a.setEstado(dto.getEstado().trim().toUpperCase());
        a.setPersona(persona);
        a.setCurso(curso);
        a.setPeriodo(periodo);
        return a;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<AsistenciaDTO>> listar() {
        ModelMapper m = mapper();
        List<AsistenciaDTO> lista = aS.list().stream()
                .map(x -> aDTO(m, x))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // Un alumno tiene un solo registro por curso y fecha (si se repite, 409)
    @PostMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<AsistenciaDTO> registrar(@Valid @RequestBody AsistenciaDTO dto) {
        ModelMapper m = mapper();
        Asistencia a = aEntidad(m, dto);
        a.setIdAsistencia(null);
        Asistencia srv = aS.insert(a);
        return ResponseEntity.status(HttpStatus.CREATED).body(aDTO(m, srv));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Asistencia> x = aS.listId(id);
        if (x.isPresent()) {
            return ResponseEntity.ok(aDTO(mapper(), x.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Asistencia no encontrada");
        }
    }

    // Asistencias de un estudiante: el personal ve las de cualquiera; el estudiante, solo las suyas
    @GetMapping("/persona/{idPersona}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL') or #idPersona.toString() == authentication.name")
    public ResponseEntity<List<AsistenciaDTO>> listarPorPersona(@PathVariable Long idPersona) {
        ModelMapper m = mapper();
        List<AsistenciaDTO> lista = aS.listarPorPersona(idPersona).stream()
                .map(x -> aDTO(m, x))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> actualizar(@Valid @RequestBody AsistenciaDTO dto) {
        if (dto.getIdAsistencia() == null || aS.listId(dto.getIdAsistencia()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Asistencia no encontrada");
        }
        ModelMapper m = mapper();
        Asistencia a = aEntidad(m, dto);
        aS.update(a);
        return ResponseEntity.ok(aDTO(m, a));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        if (aS.listId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Asistencia no encontrada");
        }
        aS.delete(id);
        return ResponseEntity.ok("Asistencia eliminada correctamente");
    }

    // Query nativo (persona + asistencia) para decidir a qué alumnos visitar o reforzar antes de que abandonen.
    // Riesgo de deserción: asistencia menor al umbral (por defecto 70 %) o promedio de notas desaprobatorio (< 11).
    @GetMapping("/reporte-participacion/{idPeriodo}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> reporteParticipacion(@PathVariable Long idPeriodo,
                                                  @RequestParam(defaultValue = "70") Double umbral) {
        if (umbral < 0 || umbral > 100) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("umbral va de 0 a 100");
        }
        List<Object[]> lista = aS.reporteParticipacion(idPeriodo);
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay asistencias en ese periodo para generar el reporte.");
        }
        List<ParticipacionEstudianteDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            ParticipacionEstudianteDTO dto = new ParticipacionEstudianteDTO();
            dto.setIdPersona(((Number) fila[0]).longValue());
            dto.setNombres((String) fila[1]);
            dto.setApellidos((String) fila[2]);
            dto.setSesiones(((Number) fila[3]).longValue());
            dto.setAsistencias(((Number) fila[4]).longValue());
            dto.setFaltas(((Number) fila[5]).longValue());
            dto.setJustificadas(((Number) fila[6]).longValue());
            dto.setPorcentajeAsistencia(fila[7] == null ? null : ((Number) fila[7]).doubleValue());
            dto.setPromedioCalificacion(fila[8] == null ? null : ((Number) fila[8]).doubleValue());
            boolean faltaMucho = dto.getPorcentajeAsistencia() != null && dto.getPorcentajeAsistencia() < umbral;
            boolean desaprueba = dto.getPromedioCalificacion() != null && dto.getPromedioCalificacion() < 11;
            dto.setRiesgoDesercion(faltaMucho || desaprueba);
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }
}
