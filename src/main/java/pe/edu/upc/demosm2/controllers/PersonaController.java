package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.CuentasPorRolDTO;
import pe.edu.upc.demosm2.dtos.PersonaDTO;
import pe.edu.upc.demosm2.dtos.PersonaRegistroDTO;
import pe.edu.upc.demosm2.entities.Persona;
import pe.edu.upc.demosm2.entities.Rol;
import pe.edu.upc.demosm2.serviceinterfaces.IPersonaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/personas")
public class PersonaController {

    @Autowired
    private IPersonaService pS;

    @GetMapping
    public ResponseEntity<List<PersonaDTO>> listar() {
        ModelMapper m = new ModelMapper();
        List<PersonaDTO> lista = pS.list().stream()
                .map(x -> m.map(x, PersonaDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // Solo el ADMIN registra personas y les pone contraseña
    @PostMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registrar(@RequestBody PersonaRegistroDTO dto) {
        if (dto.getPasswordPersona() == null || dto.getPasswordPersona().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La contraseña (passwordPersona) es obligatoria");
        }
        try {
            ModelMapper m = new ModelMapper();
            Persona p = m.map(dto, Persona.class);
            Rol r = new Rol();
            r.setIdTipoPersona(dto.getIdRol());
            p.setRol(r);
            Persona srv = pS.insert(p);
            return ResponseEntity.status(HttpStatus.CREATED).body(m.map(srv, PersonaDTO.class));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: El idRol (" + dto.getIdRol() + ") no existe en la base de datos.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        ModelMapper m = new ModelMapper();
        Optional<Persona> p = pS.listId(id);
        if (p.isPresent()) {
            return ResponseEntity.ok(m.map(p.get(), PersonaDTO.class));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Persona no encontrada");
        }
    }

    // Si passwordPersona viene vacío, se conserva la contraseña anterior
    @PutMapping("/actualiza")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizar(@RequestBody PersonaRegistroDTO dto) {
        if (dto.getIdPersona() == null || pS.listId(dto.getIdPersona()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Persona no encontrada");
        }
        try {
            ModelMapper m = new ModelMapper();
            Persona p = m.map(dto, Persona.class);
            Rol r = new Rol();
            r.setIdTipoPersona(dto.getIdRol());
            p.setRol(r);
            pS.update(p);
            return ResponseEntity.ok(m.map(p, PersonaDTO.class));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: El idRol (" + dto.getIdRol() + ") no existe en la base de datos.");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        Optional<Persona> p = pS.listId(id);
        if (p.isPresent()) {
            pS.delete(id);
            return ResponseEntity.ok("Persona eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Persona no encontrada");
        }
    }

    // Query nativo (persona + rol) para decidir: a qué roles activar cuentas o ponerles contraseña
    @GetMapping("/reporte-cuentas-por-rol")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reporteCuentasPorRol() {
        List<Object[]> lista = pS.reporteCuentasPorRol();
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay personas registradas para generar el reporte.");
        }
        List<CuentasPorRolDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            CuentasPorRolDTO dto = new CuentasPorRolDTO();
            dto.setRol((String) fila[0]);
            dto.setTotal(((Number) fila[1]).longValue());
            dto.setActivos(((Number) fila[2]).longValue());
            dto.setInactivos(((Number) fila[3]).longValue());
            dto.setSinPassword(((Number) fila[4]).longValue());
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }
}
