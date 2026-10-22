package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.EncuestaDTO;
import pe.edu.upc.demosm2.dtos.ParticipacionEncuestaDTO;
import pe.edu.upc.demosm2.entities.Encuesta;
import pe.edu.upc.demosm2.serviceinterfaces.IEncuestaService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
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

// HU16: encuestas de satisfacción al cerrar un ciclo de clases. Todos los roles las ven para poder responderlas.
@RestController
@RequestMapping("/encuestas")
public class EncuestaController {
    @Autowired
    private IEncuestaService eS;

    // fechaInicio no puede ser posterior a fechaFin; estado por defecto ABIERTA
    private Encuesta aEntidad(ModelMapper m, EncuestaDTO dto) {
        if (dto.getFechaInicio().isAfter(dto.getFechaFin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechaInicio no puede ser posterior a fechaFin");
        }
        Encuesta e = m.map(dto, Encuesta.class);
        e.setTitulo(dto.getTitulo().trim());
        e.setEstado(dto.getEstado() == null || dto.getEstado().isBlank() ? "ABIERTA" : dto.getEstado().trim().toUpperCase());
        return e;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE','ESTUDIANTE')")
    public ResponseEntity<List<EncuestaDTO>> listar() {
        ModelMapper m = new ModelMapper();
        List<EncuestaDTO> lista = eS.list().stream()
                .map(x -> m.map(x, EncuestaDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<EncuestaDTO> registrar(@Valid @RequestBody EncuestaDTO dto) {
        ModelMapper m = new ModelMapper();
        Encuesta e = aEntidad(m, dto);
        e.setIdEncuesta(null);
        Encuesta srv = eS.insert(e);
        return ResponseEntity.status(HttpStatus.CREATED).body(m.map(srv, EncuestaDTO.class));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE','ESTUDIANTE')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Encuesta> x = eS.listId(id);
        if (x.isPresent()) {
            return ResponseEntity.ok(new ModelMapper().map(x.get(), EncuestaDTO.class));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Encuesta no encontrada");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<?> actualizar(@Valid @RequestBody EncuestaDTO dto) {
        if (dto.getIdEncuesta() == null || eS.listId(dto.getIdEncuesta()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Encuesta no encontrada");
        }
        ModelMapper m = new ModelMapper();
        Encuesta e = aEntidad(m, dto);
        eS.update(e);
        return ResponseEntity.ok(m.map(e, EncuestaDTO.class));
    }

    // Si la encuesta ya tiene respuestas, la base no deja borrarla (409)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        if (eS.listId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Encuesta no encontrada");
        }
        eS.delete(id);
        return ResponseEntity.ok("Encuesta eliminada correctamente");
    }

    // Query nativo (encuesta + respuesta_encuesta) para decidir qué ciclos revisar primero:
    // los de menor satisfacción o menor participación salen arriba
    @GetMapping("/reporte-participacion")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> reporteParticipacion() {
        List<Object[]> lista = eS.reporteParticipacion();
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay encuestas registradas para generar el reporte.");
        }
        List<ParticipacionEncuestaDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            ParticipacionEncuestaDTO dto = new ParticipacionEncuestaDTO();
            dto.setIdEncuesta(((Number) fila[0]).longValue());
            dto.setTitulo((String) fila[1]);
            dto.setEstado((String) fila[2]);
            dto.setRespuestas(((Number) fila[3]).longValue());
            dto.setPromedioGeneral(fila[4] == null ? null : ((Number) fila[4]).doubleValue());
            dto.setPorcentajeSatisfechos(fila[5] == null ? null : ((Number) fila[5]).doubleValue());
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }
}
