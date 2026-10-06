package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.ConvenioDTO;
import pe.edu.upc.demosm2.dtos.ConvenioPorVencerDTO;
import pe.edu.upc.demosm2.entities.Convenio;
import pe.edu.upc.demosm2.serviceinterfaces.IConvenioService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// HU11: convenios con gobiernos regionales y cooperación internacional (vigencia, región y presupuesto).
// Los administra el ADMIN; el ADMIN_ESCUELA puede consultarlos.
@RestController
@RequestMapping("/convenios")
public class ConvenioController {
    @Autowired
    private IConvenioService cS;

    // La vigencia debe ser coherente; tipo y estado se guardan en mayúsculas (estado por defecto: VIGENTE)
    private Convenio aEntidad(ModelMapper m, ConvenioDTO dto) {
        if (!dto.getFechaInicio().isBefore(dto.getFechaFin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechaInicio debe ser anterior a fechaFin");
        }
        Convenio c = m.map(dto, Convenio.class);
        c.setTipoEntidad(dto.getTipoEntidad().trim().toUpperCase());
        c.setEstado(dto.getEstado() == null || dto.getEstado().isBlank() ? "VIGENTE" : dto.getEstado().trim().toUpperCase());
        return c;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<List<ConvenioDTO>> listar() {
        ModelMapper m = new ModelMapper();
        List<ConvenioDTO> lista = cS.list().stream()
                .map(x -> m.map(x, ConvenioDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConvenioDTO> registrar(@Valid @RequestBody ConvenioDTO dto) {
        ModelMapper m = new ModelMapper();
        Convenio c = aEntidad(m, dto);
        c.setIdConvenio(null);
        Convenio srv = cS.insert(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(m.map(srv, ConvenioDTO.class));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Convenio> x = cS.listId(id);
        if (x.isPresent()) {
            return ResponseEntity.ok(new ModelMapper().map(x.get(), ConvenioDTO.class));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Convenio no encontrado");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizar(@Valid @RequestBody ConvenioDTO dto) {
        if (dto.getIdConvenio() == null || cS.listId(dto.getIdConvenio()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Convenio no encontrado");
        }
        ModelMapper m = new ModelMapper();
        Convenio c = aEntidad(m, dto);
        cS.update(c);
        return ResponseEntity.ok(m.map(c, ConvenioDTO.class));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        if (cS.listId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Convenio no encontrado");
        }
        cS.delete(id);
        return ResponseEntity.ok("Convenio eliminado correctamente");
    }

    // Query nativo (convenio) para decidir qué convenios renovar: vencidos y los que vencen en los próximos "dias" días
    @GetMapping("/reporte-por-vencer")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<?> reportePorVencer(@RequestParam(defaultValue = "90") Integer dias) {
        if (dias < 0 || dias > 3650) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("dias va de 0 a 3650");
        }
        List<Object[]> lista = cS.reportePorVencer(dias);
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay convenios vencidos ni por vencer en ese plazo.");
        }
        List<ConvenioPorVencerDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            ConvenioPorVencerDTO dto = new ConvenioPorVencerDTO();
            dto.setIdConvenio(((Number) fila[0]).longValue());
            dto.setNombre((String) fila[1]);
            dto.setEntidad((String) fila[2]);
            dto.setRegion((String) fila[3]);
            dto.setFechaFin(aFecha(fila[4]));
            dto.setDiasRestantes(((Number) fila[5]).longValue());
            dto.setPresupuesto(fila[6] == null ? null : new BigDecimal(fila[6].toString()));
            dto.setEstado((String) fila[7]);
            dto.setSituacion((String) fila[8]);
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }

    private static LocalDate aFecha(Object valor) {
        if (valor == null) return null;
        if (valor instanceof LocalDate f) return f;
        return ((java.sql.Date) valor).toLocalDate();
    }
}
