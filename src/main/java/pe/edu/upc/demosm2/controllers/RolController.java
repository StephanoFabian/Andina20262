package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.RolDTO;
import pe.edu.upc.demosm2.dtos.RolCantidadDTO;
import pe.edu.upc.demosm2.entities.Rol;
import pe.edu.upc.demosm2.serviceinterfaces.IRolService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roles")
public class RolController {
    @Autowired
    private IRolService rS;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<RolDTO>> listar() {
        ModelMapper m = new ModelMapper();
        List<RolDTO> lista = rS.list().stream()
                .map(x -> m.map(x, RolDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RolDTO> registrar(@Valid @RequestBody RolDTO dto) {
        ModelMapper m = new ModelMapper();
        Rol x = m.map(dto, Rol.class);
        x.setIdTipoPersona(null);
        Rol srv = rS.insert(x);
        return ResponseEntity.status(HttpStatus.CREATED).body(m.map(srv, RolDTO.class));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        ModelMapper m = new ModelMapper();
        Optional<Rol> x = rS.listId(id);
        if (x.isPresent()) {
            return ResponseEntity.ok(m.map(x.get(), RolDTO.class));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizar(@Valid @RequestBody RolDTO dto) {
        if (dto.getIdTipoPersona() == null || rS.listId(dto.getIdTipoPersona()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado");
        }
        ModelMapper m = new ModelMapper();
        Rol x = m.map(dto, Rol.class);
        rS.update(x);
        return ResponseEntity.ok(m.map(x, RolDTO.class));
    }

    // HU37: no se elimina un rol que todavía tiene personas asociadas (409)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        Optional<Rol> x = rS.listId(id);
        if (x.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado");
        }
        long personas = rS.contarPersonasDelRol(id);
        if (personas > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar el rol: tiene " + personas + " persona(s) asociada(s)");
        }
        rS.delete(id);
        return ResponseEntity.ok("Rol eliminado correctamente");
    }

    // Query nativo (rol + persona) para decidir si falta personal de algún tipo
    @GetMapping("/reporte-cantidad-personas")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> reporteCantidadPersonasPorRol() {
        List<Object[]> lista = rS.reporteCantidadPersonasPorRol();
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay roles registrados para generar el reporte.");
        }
        List<RolCantidadDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            RolCantidadDTO dto = new RolCantidadDTO();
            dto.setRol((String) fila[0]);
            dto.setCantidad(((Number) fila[1]).longValue());
            dto.setPorcentaje(fila[2] == null ? null : ((Number) fila[2]).doubleValue());
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }
}
