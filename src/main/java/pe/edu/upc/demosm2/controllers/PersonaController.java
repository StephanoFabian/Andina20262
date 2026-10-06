package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.CuentasPorRolDTO;
import pe.edu.upc.demosm2.dtos.PersonaAulaDTO;
import pe.edu.upc.demosm2.dtos.PersonaDTO;
import pe.edu.upc.demosm2.dtos.PersonaRegistroDTO;
import pe.edu.upc.demosm2.entities.Aula;
import pe.edu.upc.demosm2.entities.Persona;
import pe.edu.upc.demosm2.entities.Rol;
import pe.edu.upc.demosm2.serviceinterfaces.AulaServiceInterface;
import pe.edu.upc.demosm2.serviceinterfaces.IAuditoriaService;
import pe.edu.upc.demosm2.serviceinterfaces.IPersonaService;
import pe.edu.upc.demosm2.serviceinterfaces.IRolService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/personas")
public class PersonaController {
    @Autowired
    private IPersonaService pS;

    @Autowired
    private IRolService rS;

    @Autowired
    private AulaServiceInterface aS;

    @Autowired
    private IAuditoriaService auditoria;

    // Entidad -> DTO: ModelMapper copia los datos y los id de rol y aula se ponen aparte
    private PersonaDTO aDTO(ModelMapper m, Persona p) {
        PersonaDTO dto = m.map(p, PersonaDTO.class);
        dto.setIdRol(p.getRol() == null ? null : p.getRol().getIdTipoPersona());
        dto.setIdAula(p.getAula() == null ? null : p.getAula().getIdAula());
        return dto;
    }

    // DTO -> entidad: el rol y el aula deben existir (HU45: si no, 400)
    private Persona aEntidad(ModelMapper m, PersonaRegistroDTO dto) {
        Rol rol = rS.listId(dto.getIdRol()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Error: El idRol (" + dto.getIdRol() + ") no existe en la base de datos."));
        Aula aula = null;
        if (dto.getIdAula() != null) {
            aula = aS.listId(dto.getIdAula()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error: El idAula (" + dto.getIdAula() + ") no existe en la base de datos."));
        }
        Persona p = m.map(dto, Persona.class);
        p.setRol(rol);
        p.setAula(aula);
        if (p.getEstadoPersona() != null) p.setEstadoPersona(p.getEstadoPersona().trim().toUpperCase());
        return p;
    }

    // HU47: sin filtros lista a todas; con idAula y/o estado filtra en la base de datos (lista vacía si nada coincide).
    // Un estudiante no puede ver el directorio de personas.
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<PersonaDTO>> listar(@RequestParam(required = false) Long idAula,
                                                   @RequestParam(required = false) String estado) {
        ModelMapper m = new ModelMapper();
        String filtroEstado = (estado == null || estado.isBlank()) ? null : estado.trim();
        List<Persona> personas = (idAula == null && filtroEstado == null)
                ? pS.list()
                : pS.filtrarPorAulaYEstado(idAula, filtroEstado);
        List<PersonaDTO> lista = personas.stream()
                .map(x -> aDTO(m, x))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // Solo el ADMIN registra personas y les pone contraseña. Si no se envía estado, queda ACTIVO (HU45).
    @PostMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registrar(@Valid @RequestBody PersonaRegistroDTO dto, Authentication auth,
                                       HttpServletRequest request) {
        if (dto.getPasswordPersona() == null || dto.getPasswordPersona().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La contraseña (passwordPersona) es obligatoria");
        }
        ModelMapper m = new ModelMapper();
        Persona p = aEntidad(m, dto);
        p.setIdPersona(null); // el ID lo genera la base: un POST no puede sobrescribir a otra persona
        if (p.getEstadoPersona() == null || p.getEstadoPersona().isBlank()) p.setEstadoPersona("ACTIVO");
        Persona srv = pS.insert(p);
        auditoria.registrar(auth.getName(), "PERSONAS", "CAMBIO_PASSWORD",
                "Contraseña inicial de la persona " + srv.getIdPersona(), request.getRemoteAddr(), 201);
        return ResponseEntity.status(HttpStatus.CREATED).body(aDTO(m, srv));
    }

    // El personal ve a cualquiera; cada persona puede ver sus propios datos
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL') or #id.toString() == authentication.name")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        ModelMapper m = new ModelMapper();
        Optional<Persona> p = pS.listId(id);
        if (p.isPresent()) {
            return ResponseEntity.ok(aDTO(m, p.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Persona no encontrada");
        }
    }

    // Si passwordPersona viene vacío se conserva la contraseña anterior; si estado viene vacío, se conserva el anterior
    @PutMapping("/actualiza")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizar(@Valid @RequestBody PersonaRegistroDTO dto, Authentication auth,
                                        HttpServletRequest request) {
        Optional<Persona> actual = dto.getIdPersona() == null ? Optional.empty() : pS.listId(dto.getIdPersona());
        if (actual.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Persona no encontrada");
        }
        String estadoAnterior = actual.get().getEstadoPersona();
        ModelMapper m = new ModelMapper();
        Persona p = aEntidad(m, dto);
        if (p.getEstadoPersona() == null || p.getEstadoPersona().isBlank()) p.setEstadoPersona(estadoAnterior);
        pS.update(p);
        if (dto.getPasswordPersona() != null && !dto.getPasswordPersona().isBlank()) {
            auditoria.registrar(auth.getName(), "PERSONAS", "CAMBIO_PASSWORD",
                    "Contraseña cambiada de la persona " + p.getIdPersona(), request.getRemoteAddr(), 200);
        }
        return ResponseEntity.ok(aDTO(m, p));
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

    // HU44: reasignación rápida de aula. Solo cambia id_aula (el aula debe existir) y devuelve la persona actualizada.
    @PatchMapping("/{id}/aula")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<?> reasignarAula(@PathVariable Long id, @Valid @RequestBody PersonaAulaDTO dto) {
        Optional<Persona> p = pS.listId(id);
        if (p.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Persona no encontrada");
        }
        Optional<Aula> aula = aS.listId(dto.getIdAula());
        if (aula.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El aula (" + dto.getIdAula() + ") no existe");
        }
        Persona persona = p.get();
        persona.setAula(aula.get());
        Persona srv = pS.reasignarAula(persona);
        return ResponseEntity.ok(aDTO(new ModelMapper(), srv));
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
