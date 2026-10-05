package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.PerfilAcademicoDTO;
import pe.edu.upc.demosm2.dtos.PerfilAcademicoParcialDTO;
import pe.edu.upc.demosm2.dtos.PerfilAcademicoParcialRespuestaDTO;
import pe.edu.upc.demosm2.entities.PerfilAcademico;
import pe.edu.upc.demosm2.entities.Persona;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.serviceinterfaces.IPerfilAcademicoService;
import pe.edu.upc.demosm2.serviceinterfaces.IPersonaService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// HU33/HU39/HU46: datos confidenciales (notas y estado psicológico): todo el controller es solo para el personal autorizado
@RestController
@RequestMapping("/perfiles-academicos")
public class PerfilAcademicoController {
    @Autowired
    private IPerfilAcademicoService paS;

    @Autowired
    private IPersonaService pS;

    // Entidad -> DTO: ModelMapper copia los datos y el id de la persona se pone aparte
    private PerfilAcademicoDTO aDTO(ModelMapper m, PerfilAcademico x) {
        PerfilAcademicoDTO dto = m.map(x, PerfilAcademicoDTO.class);
        dto.setIdPersona(x.getPersona() == null ? null : x.getPersona().getIdPersona());
        return dto;
    }

    // El perfil pertenece a una persona que exista y sea ESTUDIANTE
    private Persona estudiante(Long idPersona) {
        Persona p = pS.listId(idPersona)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la persona: " + idPersona));
        if (!pS.tieneAlgunRol(p, "ESTUDIANTE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El perfil académico solo se registra para personas con rol ESTUDIANTE");
        }
        return p;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<PerfilAcademicoDTO>> listar() {
        ModelMapper m = new ModelMapper();
        List<PerfilAcademicoDTO> lista = paS.list().stream()
                .map(x -> aDTO(m, x))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // Un estudiante tiene un solo perfil (409 si ya existe)
    @PostMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> registrar(@Valid @RequestBody PerfilAcademicoDTO dto) {
        Persona persona = estudiante(dto.getIdPersona());
        if (paS.buscarPorPersona(dto.getIdPersona()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("La persona " + dto.getIdPersona() + " ya tiene perfil académico");
        }
        ModelMapper m = new ModelMapper();
        PerfilAcademico x = m.map(dto, PerfilAcademico.class);
        x.setIdPerfilAcademico(null);
        x.setPersona(persona);
        PerfilAcademico srv = paS.insert(x);
        return ResponseEntity.status(HttpStatus.CREATED).body(aDTO(m, srv));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        ModelMapper m = new ModelMapper();
        Optional<PerfilAcademico> x = paS.listId(id);
        if (x.isPresent()) {
            return ResponseEntity.ok(aDTO(m, x.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil académico no encontrado");
        }
    }

    // Perfil académico de un estudiante
    @GetMapping("/persona/{idPersona}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> buscarPorPersona(@PathVariable Long idPersona) {
        Optional<PerfilAcademico> x = paS.buscarPorPersona(idPersona);
        if (x.isPresent()) {
            return ResponseEntity.ok(aDTO(new ModelMapper(), x.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La persona " + idPersona + " no tiene perfil académico");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> actualizar(@Valid @RequestBody PerfilAcademicoDTO dto) {
        if (dto.getIdPerfilAcademico() == null || paS.listId(dto.getIdPerfilAcademico()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil académico no encontrado");
        }
        Persona persona = estudiante(dto.getIdPersona());
        Optional<PerfilAcademico> otro = paS.buscarPorPersona(dto.getIdPersona());
        if (otro.isPresent() && !otro.get().getIdPerfilAcademico().equals(dto.getIdPerfilAcademico())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("La persona " + dto.getIdPersona() + " ya tiene otro perfil académico");
        }
        ModelMapper m = new ModelMapper();
        PerfilAcademico x = m.map(dto, PerfilAcademico.class);
        x.setPersona(persona);
        paS.update(x);
        return ResponseEntity.ok(aDTO(m, x));
    }

    // HU46: actualización parcial de la nota y/o el estado psicológico del estudiante.
    // Solo cambia lo que llega con valor y responde qué campos cambió y cuándo.
    @PatchMapping("/persona/{idPersona}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> actualizarParcial(@PathVariable Long idPersona, @Valid @RequestBody PerfilAcademicoParcialDTO dto) {
        if (dto.getNotasPA() == null && (dto.getEstadoPsicologico() == null || dto.getEstadoPsicologico().isBlank())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Envía notasPA y/o estadoPsicologico");
        }
        Optional<PerfilAcademico> x = paS.buscarPorPersona(idPersona);
        if (x.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La persona " + idPersona + " no tiene perfil académico");
        }
        PerfilAcademico perfil = x.get();
        List<String> cambios = new ArrayList<>();
        if (dto.getNotasPA() != null) {
            perfil.setNotasPA(dto.getNotasPA());
            cambios.add("notasPA");
        }
        if (dto.getEstadoPsicologico() != null && !dto.getEstadoPsicologico().isBlank()) {
            perfil.setEstadoPsicologico(dto.getEstadoPsicologico().trim());
            cambios.add("estadoPsicologico");
        }
        paS.update(perfil);
        PerfilAcademicoParcialRespuestaDTO respuesta = new PerfilAcademicoParcialRespuestaDTO();
        respuesta.setIdPerfilAcademico(perfil.getIdPerfilAcademico());
        respuesta.setIdPersona(idPersona);
        respuesta.setCamposModificados(cambios);
        respuesta.setFechaActualizacion(LocalDateTime.now());
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        Optional<PerfilAcademico> x = paS.listId(id);
        if (x.isPresent()) {
            paS.delete(id);
            return ResponseEntity.ok("Perfil académico eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil académico no encontrado");
        }
    }

    // Query nativo: perfiles con nota menor a la indicada (alumnos a los que apoyar)
    @GetMapping("/nota-menor/{nota}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<PerfilAcademicoDTO>> listarPerfilesConNotaMenorA(@PathVariable Double nota) {
        ModelMapper m = new ModelMapper();
        List<PerfilAcademicoDTO> lista = paS.listarPerfilesConNotaMenorA(nota).stream()
                .map(x -> aDTO(m, x))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }
}
