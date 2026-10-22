package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.RespuestaEncuestaDTO;
import pe.edu.upc.demosm2.dtos.SatisfaccionPorRolDTO;
import pe.edu.upc.demosm2.entities.Encuesta;
import pe.edu.upc.demosm2.entities.Persona;
import pe.edu.upc.demosm2.entities.RespuestaEncuesta;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.serviceinterfaces.IEncuestaService;
import pe.edu.upc.demosm2.serviceinterfaces.IPersonaService;
import pe.edu.upc.demosm2.serviceinterfaces.IRespuestaEncuestaService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// HU16: respuestas de estudiantes, docentes y especialistas (escala Likert 1-5 y comentario abierto)
@RestController
@RequestMapping("/respuestas-encuesta")
public class RespuestaEncuestaController {
    @Autowired
    private IRespuestaEncuestaService reS;

    @Autowired
    private IEncuestaService eS;

    @Autowired
    private IPersonaService pS;

    private ModelMapper mapper() {
        ModelMapper m = new ModelMapper();
        m.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return m;
    }

    private RespuestaEncuestaDTO aDTO(ModelMapper m, RespuestaEncuesta r) {
        RespuestaEncuestaDTO dto = m.map(r, RespuestaEncuestaDTO.class);
        dto.setIdEncuesta(r.getEncuesta().getIdEncuesta());
        dto.setIdPersona(r.getPersona().getIdPersona());
        return dto;
    }

    // Responder: quien responde sale del token; la encuesta debe estar ABIERTA y dentro de sus fechas;
    // cada persona responde una sola vez (409 si repite)
    @PostMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE','ESTUDIANTE')")
    public ResponseEntity<?> registrar(@Valid @RequestBody RespuestaEncuestaDTO dto, Authentication auth) {
        Long idPersona;
        try {
            idPersona = Long.parseLong(auth.getName());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El token no corresponde a una persona");
        }
        Persona persona = pS.listId(idPersona)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la persona del token: " + auth.getName()));
        Encuesta encuesta = eS.listId(dto.getIdEncuesta())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la encuesta: " + dto.getIdEncuesta()));
        LocalDate hoy = LocalDate.now();
        if (!"ABIERTA".equalsIgnoreCase(encuesta.getEstado()) || hoy.isBefore(encuesta.getFechaInicio()) || hoy.isAfter(encuesta.getFechaFin())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La encuesta no está abierta para respuestas");
        }
        if (reS.contarRespuestas(encuesta.getIdEncuesta(), idPersona) > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya respondiste esta encuesta");
        }
        ModelMapper m = mapper();
        RespuestaEncuesta r = m.map(dto, RespuestaEncuesta.class);
        r.setIdRespuesta(null);
        r.setEncuesta(encuesta);
        r.setPersona(persona);
        r.setFechaRespuesta(LocalDateTime.now());
        RespuestaEncuesta srv = reS.insert(r);
        return ResponseEntity.status(HttpStatus.CREATED).body(aDTO(m, srv));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<RespuestaEncuestaDTO>> listar() {
        ModelMapper m = mapper();
        List<RespuestaEncuestaDTO> lista = reS.list().stream()
                .map(x -> aDTO(m, x))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<RespuestaEncuesta> x = reS.listId(id);
        if (x.isPresent()) {
            return ResponseEntity.ok(aDTO(mapper(), x.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Respuesta no encontrada");
        }
    }

    // Respuestas de una encuesta, la más reciente primero
    @GetMapping("/encuesta/{idEncuesta}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<RespuestaEncuestaDTO>> listarPorEncuesta(@PathVariable Long idEncuesta) {
        ModelMapper m = mapper();
        List<RespuestaEncuestaDTO> lista = reS.listarPorEncuesta(idEncuesta).stream()
                .map(x -> aDTO(m, x))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        if (reS.listId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Respuesta no encontrada");
        }
        reS.delete(id);
        return ResponseEntity.ok("Respuesta eliminada correctamente");
    }

    // Query nativo (respuesta_encuesta + persona + rol) para decidir qué perfil está menos satisfecho con el servicio
    @GetMapping("/reporte-satisfaccion/{idEncuesta}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> reporteSatisfaccionPorRol(@PathVariable Long idEncuesta) {
        List<Object[]> lista = reS.reporteSatisfaccionPorRol(idEncuesta);
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La encuesta no tiene respuestas para generar el reporte.");
        }
        List<SatisfaccionPorRolDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            SatisfaccionPorRolDTO dto = new SatisfaccionPorRolDTO();
            dto.setRol((String) fila[0]);
            dto.setRespuestas(((Number) fila[1]).longValue());
            dto.setPromedioSatisfaccion(((Number) fila[2]).doubleValue());
            dto.setPromedioContenido(((Number) fila[3]).doubleValue());
            dto.setPromedioFacilidad(((Number) fila[4]).doubleValue());
            dto.setPorcentajeSatisfechos(((Number) fila[5]).doubleValue());
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }
}
