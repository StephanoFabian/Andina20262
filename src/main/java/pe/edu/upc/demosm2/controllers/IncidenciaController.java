package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.IncidenciaDTO;
import pe.edu.upc.demosm2.dtos.IncidenciaEstadoDTO;
import pe.edu.upc.demosm2.dtos.IncidenciasPorPrioridadDTO;
import pe.edu.upc.demosm2.entities.Colegio;
import pe.edu.upc.demosm2.entities.Incidencia;
import pe.edu.upc.demosm2.entities.Persona;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.serviceinterfaces.ColegioServiceInterface;
import pe.edu.upc.demosm2.serviceinterfaces.IIncidenciaService;
import pe.edu.upc.demosm2.serviceinterfaces.IPersonaService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// HU14: tickets de soporte técnico. Los reportan docentes y personal; el equipo técnico (administradores) los atiende.
@RestController
@RequestMapping("/incidencias")
public class IncidenciaController {

    private static final Logger log = LoggerFactory.getLogger(IncidenciaController.class);

    @Autowired
    private IIncidenciaService iS;

    @Autowired
    private IPersonaService pS;

    @Autowired
    private ColegioServiceInterface coS;

    private ModelMapper mapper() {
        ModelMapper m = new ModelMapper();
        m.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return m;
    }

    private IncidenciaDTO aDTO(ModelMapper m, Incidencia i) {
        IncidenciaDTO dto = m.map(i, IncidenciaDTO.class);
        dto.setIdPersona(i.getPersona().getIdPersona());
        dto.setIdColegio(i.getColegio() == null ? null : i.getColegio().getIdColegio());
        dto.setAfectaSesion(i.isAfectaSesion());
        return dto;
    }

    // Quien reporta sale del token (su ID de persona), no del cuerpo de la petición
    private Persona personaDelToken(Authentication auth) {
        Long id;
        try {
            id = Long.parseLong(auth.getName());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El token no corresponde a una persona");
        }
        return pS.listId(id).orElseThrow(() -> new ResourceNotFoundException("No existe la persona del token: " + id));
    }

    private Colegio colegio(Long idColegio) {
        if (idColegio == null) return null;
        return coS.listId(idColegio).orElseThrow(() -> new ResourceNotFoundException("No existe el colegio: " + idColegio));
    }

    // Registrar una incidencia: queda ABIERTA. Las CRITICA generan una alerta para el equipo técnico.
    @PostMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE')")
    public ResponseEntity<IncidenciaDTO> registrar(@Valid @RequestBody IncidenciaDTO dto, Authentication auth) {
        ModelMapper m = mapper();
        Incidencia i = new Incidencia();
        i.setPersona(personaDelToken(auth));
        i.setColegio(colegio(dto.getIdColegio()));
        i.setTitulo(dto.getTitulo().trim());
        i.setDescripcion(dto.getDescripcion().trim());
        i.setPrioridad(dto.getPrioridad().trim().toUpperCase());
        i.setAfectaSesion(Boolean.TRUE.equals(dto.getAfectaSesion()));
        i.setEstado("ABIERTA");
        i.setFechaReporte(LocalDateTime.now());
        Incidencia srv = iS.insert(i);
        if ("CRITICA".equals(srv.getPrioridad())) {
            // Aviso en el log del servidor; el equipo técnico ve la lista en GET /incidencias/alertas-criticas
            log.warn("ALERTA: incidencia CRITICA #{} '{}' (afecta sesión en curso: {})",
                    srv.getIdIncidencia(), srv.getTitulo(), srv.isAfectaSesion());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(aDTO(m, srv));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<List<IncidenciaDTO>> listar() {
        ModelMapper m = mapper();
        List<IncidenciaDTO> lista = iS.list().stream()
                .map(x -> aDTO(m, x))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // Incidencias que reportó la persona que hace la consulta
    @GetMapping("/mias")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE')")
    public ResponseEntity<List<IncidenciaDTO>> listarMias(Authentication auth) {
        ModelMapper m = mapper();
        List<IncidenciaDTO> lista = iS.listarPorPersona(personaDelToken(auth).getIdPersona()).stream()
                .map(x -> aDTO(m, x))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // Alertas para el equipo técnico: críticas sin resolver, primero las que afectan una sesión en curso
    @GetMapping("/alertas-criticas")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<List<IncidenciaDTO>> alertasCriticas() {
        ModelMapper m = mapper();
        List<IncidenciaDTO> lista = iS.listarCriticasPendientes().stream()
                .map(x -> aDTO(m, x))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Incidencia> x = iS.listId(id);
        if (x.isPresent()) {
            return ResponseEntity.ok(aDTO(mapper(), x.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incidencia no encontrada");
        }
    }

    // Corrige los datos del ticket (título, descripción, prioridad, colegio); el estado se cambia con PATCH
    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<?> actualizar(@Valid @RequestBody IncidenciaDTO dto) {
        Optional<Incidencia> x = dto.getIdIncidencia() == null ? Optional.empty() : iS.listId(dto.getIdIncidencia());
        if (x.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incidencia no encontrada");
        }
        Incidencia i = x.get();
        i.setColegio(colegio(dto.getIdColegio()));
        i.setTitulo(dto.getTitulo().trim());
        i.setDescripcion(dto.getDescripcion().trim());
        i.setPrioridad(dto.getPrioridad().trim().toUpperCase());
        i.setAfectaSesion(Boolean.TRUE.equals(dto.getAfectaSesion()));
        iS.update(i);
        return ResponseEntity.ok(aDTO(mapper(), i));
    }

    // Cambiar el estado (EN_PROCESO, RESUELTA, CERRADA…); al resolver o cerrar se guarda la fecha de cierre
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @Valid @RequestBody IncidenciaEstadoDTO dto) {
        Optional<Incidencia> x = iS.listId(id);
        if (x.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incidencia no encontrada");
        }
        Incidencia i = x.get();
        String estado = dto.getEstado().trim().toUpperCase();
        i.setEstado(estado);
        boolean cerrada = estado.equals("RESUELTA") || estado.equals("CERRADA");
        i.setFechaCierre(cerrada ? (i.getFechaCierre() == null ? LocalDateTime.now() : i.getFechaCierre()) : null);
        iS.update(i);
        return ResponseEntity.ok(aDTO(mapper(), i));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        if (iS.listId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incidencia no encontrada");
        }
        iS.delete(id);
        return ResponseEntity.ok("Incidencia eliminada correctamente");
    }

    // Query nativo (incidencia) para decidir dónde poner más soporte: pendientes y horas de resolución por prioridad
    @GetMapping("/reporte-por-prioridad")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<?> reportePorPrioridad() {
        List<Object[]> lista = iS.reportePorPrioridad();
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay incidencias registradas para generar el reporte.");
        }
        List<IncidenciasPorPrioridadDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            IncidenciasPorPrioridadDTO dto = new IncidenciasPorPrioridadDTO();
            dto.setPrioridad((String) fila[0]);
            dto.setTotal(((Number) fila[1]).longValue());
            dto.setPendientes(((Number) fila[2]).longValue());
            dto.setResueltas(((Number) fila[3]).longValue());
            dto.setHorasPromedioResolucion(fila[4] == null ? null : ((Number) fila[4]).doubleValue());
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }
}
