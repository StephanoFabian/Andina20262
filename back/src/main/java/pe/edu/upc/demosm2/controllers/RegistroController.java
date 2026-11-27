package pe.edu.upc.demosm2.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.demosm2.dtos.PersonaDTO;
import pe.edu.upc.demosm2.dtos.RegistroPublicoDTO;
import pe.edu.upc.demosm2.entities.Persona;
import pe.edu.upc.demosm2.entities.Rol;
import pe.edu.upc.demosm2.serviceinterfaces.IPersonaService;
import pe.edu.upc.demosm2.serviceinterfaces.IRolService;

import java.util.Optional;

// Registro público desde el landing page (sin token). Es la única forma de crear una cuenta
// sin ser ADMIN, por eso el rol NO viene del cliente: siempre se crea un ESTUDIANTE ACTIVO.
// Las demás cuentas (docentes, administradores...) las sigue creando el ADMIN en /personas/nuevo.
@RestController
public class RegistroController {

    private static final String ROL_REGISTRO = "ESTUDIANTE";

    @Autowired
    private IPersonaService pS;

    @Autowired
    private IRolService rS;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroPublicoDTO dto) {
        String email = dto.getEmailPersona().trim().toLowerCase();
        if (pS.contarPorEmail(email) > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe una cuenta con ese correo");
        }

        // Se usa el rol ESTUDIANTE con el id más bajo (la tabla puede tener roles repetidos)
        Optional<Rol> rol = rS.list().stream()
                .filter(r -> r.getDetalle() != null && r.getDetalle().trim().equalsIgnoreCase(ROL_REGISTRO))
                .min((a, b) -> a.getIdTipoPersona().compareTo(b.getIdTipoPersona()));
        if (rol.isEmpty()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("El registro no está disponible: falta el rol " + ROL_REGISTRO + " en la base de datos");
        }

        Persona p = new Persona();
        p.setNombresPersona(dto.getNombresPersona().trim());
        p.setApellidosPersona(dto.getApellidosPersona().trim());
        p.setFechaNacimientoPersona(dto.getFechaNacimientoPersona());
        p.setEmailPersona(email);
        p.setEstadoPersona("ACTIVO");
        p.setPasswordPersona(dto.getPasswordPersona());
        p.setRol(rol.get());

        // insert() guarda solo el hash BCrypt de la contraseña
        Persona srv = pS.insert(p);

        ModelMapper m = new ModelMapper();
        return ResponseEntity.status(HttpStatus.CREATED).body(m.map(srv, PersonaDTO.class));
    }
}
