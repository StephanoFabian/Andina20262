package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.CapacitacionDocenteDTO;
import pe.edu.upc.demosm2.dtos.PreparacionDigitalDTO;
import pe.edu.upc.demosm2.entities.CapacitacionDocente;
import pe.edu.upc.demosm2.entities.Persona;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.serviceinterfaces.ICapacitacionDocenteService;
import pe.edu.upc.demosm2.serviceinterfaces.IPersonaService;
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

// HU13: evaluaciones de los docentes en los talleres de competencias digitales y su preparación por escuela y región
@RestController
@RequestMapping("/capacitaciones-docentes")
public class CapacitacionDocenteController {
    @Autowired
    private ICapacitacionDocenteService cdS;

    @Autowired
    private IPersonaService pS;

    private ModelMapper mapper() {
        ModelMapper m = new ModelMapper();
        m.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return m;
    }

    private CapacitacionDocenteDTO aDTO(ModelMapper m, CapacitacionDocente c) {
        CapacitacionDocenteDTO dto = m.map(c, CapacitacionDocenteDTO.class);
        dto.setIdPersona(c.getPersona().getIdPersona());
        return dto;
    }

    // La persona debe ser docente. El estado sale del puntaje: sin puntaje EN_CURSO, con 11 o más APROBADO.
    private CapacitacionDocente aEntidad(ModelMapper m, CapacitacionDocenteDTO dto) {
        Persona docente = pS.listId(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la persona: " + dto.getIdPersona()));
        if (!pS.tieneAlgunRol(docente, "DOCENTE", "ESPECIALISTA", "LOCAL")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La capacitación se registra solo para personas con rol DOCENTE, ESPECIALISTA o LOCAL");
        }
        CapacitacionDocente c = m.map(dto, CapacitacionDocente.class);
        c.setModulo(dto.getModulo().trim());
        c.setPersona(docente);
        if (c.getPuntaje() == null) c.setEstado("EN_CURSO");
        else c.setEstado(c.getPuntaje() >= 11 ? "APROBADO" : "DESAPROBADO");
        return c;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<CapacitacionDocenteDTO>> listar() {
        ModelMapper m = mapper();
        List<CapacitacionDocenteDTO> lista = cdS.list().stream()
                .map(x -> aDTO(m, x))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA')")
    public ResponseEntity<CapacitacionDocenteDTO> registrar(@Valid @RequestBody CapacitacionDocenteDTO dto) {
        ModelMapper m = mapper();
        CapacitacionDocente c = aEntidad(m, dto);
        c.setIdCapacitacion(null);
        CapacitacionDocente srv = cdS.insert(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(aDTO(m, srv));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<CapacitacionDocente> x = cdS.listId(id);
        if (x.isPresent()) {
            return ResponseEntity.ok(aDTO(mapper(), x.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Capacitación no encontrada");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA')")
    public ResponseEntity<?> actualizar(@Valid @RequestBody CapacitacionDocenteDTO dto) {
        if (dto.getIdCapacitacion() == null || cdS.listId(dto.getIdCapacitacion()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Capacitación no encontrada");
        }
        ModelMapper m = mapper();
        CapacitacionDocente c = aEntidad(m, dto);
        cdS.update(c);
        return ResponseEntity.ok(aDTO(m, c));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA')")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        if (cdS.listId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Capacitación no encontrada");
        }
        cdS.delete(id);
        return ResponseEntity.ok("Capacitación eliminada correctamente");
    }

    // Query nativo (capacitacion_docente + asignacion_docente + colegios) para decidir en qué escuelas y regiones
    // reforzar la capacitación digital: las de menor % de aprobación salen primero
    @GetMapping("/reporte-preparacion")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> reportePreparacion() {
        List<Object[]> lista = cdS.reportePreparacionPorColegio();
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No hay docentes con capacitaciones y asignaciones para generar el reporte.");
        }
        List<PreparacionDigitalDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            PreparacionDigitalDTO dto = new PreparacionDigitalDTO();
            dto.setRegion((String) fila[0]);
            dto.setColegio((String) fila[1]);
            dto.setDocentesEvaluados(((Number) fila[2]).longValue());
            dto.setEvaluaciones(((Number) fila[3]).longValue());
            dto.setPromedioPuntaje(fila[4] == null ? null : ((Number) fila[4]).doubleValue());
            dto.setAprobadas(((Number) fila[5]).longValue());
            dto.setPorcentajeAprobacion(fila[6] == null ? null : ((Number) fila[6]).doubleValue());
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }
}
