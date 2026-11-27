package pe.edu.upc.demosm2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.demosm2.controllers.JwtAuthenticationController;
import pe.edu.upc.demosm2.dtos.JwtRequestDTO;
import pe.edu.upc.demosm2.dtos.JwtResponseDTO;
import pe.edu.upc.demosm2.entities.Persona;
import pe.edu.upc.demosm2.entities.Rol;
import pe.edu.upc.demosm2.repositories.IRolRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IPersonaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JwtLoginTests {
    @Autowired private JwtAuthenticationController login;
    @Autowired private IPersonaService personas;
    @Autowired private IRolRepository roles;
    @Autowired private JwtDecoder decoder;

    @Test
    void loginPorCorreoEmiteElIdDeLaPersonaYSuRol() {
        String correo = UUID.randomUUID() + "@andina.example";
        String clave = "ClaveDePrueba";
        Rol rol = roles.save(new Rol(null, "ESTUDIANTE"));
        Persona persona = personas.insert(new Persona(null, "Ana", "Prueba", LocalDate.of(2000, 1, 1),
                correo, "ACTIVO", clave, rol));
        JwtRequestDTO solicitud = new JwtRequestDTO();
        solicitud.setEmailPersona(" " + correo.toUpperCase(Locale.ROOT) + " ");
        solicitud.setPasswordPersona(clave);

        var respuesta = login.login(solicitud);
        assertEquals(200, respuesta.getStatusCode().value());
        var token = assertInstanceOf(JwtResponseDTO.class, respuesta.getBody()).getToken();
        var jwt = decoder.decode(token);
        assertEquals(persona.getIdPersona().toString(), jwt.getSubject());
        assertEquals(List.of("ROLE_ESTUDIANTE"), jwt.getClaimAsStringList("roles"));
    }
}
