package pe.edu.upc.demosm2.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.upc.demosm2.dtos.*;
import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.serviceinterfaces.MatriculaServiceInterface;
import pe.edu.upc.demosm2.repositories.IPersonaRepositories;
import pe.edu.upc.demosm2.serviceinterfaces.ColegioServiceInterface;
import pe.edu.upc.demosm2.serviceinterfaces.IPersonaService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {
    private final MatriculaServiceInterface service;
    private final ColegioServiceInterface colegioService;
    private final IPersonaRepositories personaRepository;
    private final IPersonaService personaService;

    public MatriculaController(MatriculaServiceInterface service, ColegioServiceInterface colegioService,
                               IPersonaRepositories personaRepository, IPersonaService personaService) {
        this.service = service;
        this.colegioService = colegioService;
        this.personaRepository = personaRepository;
        this.personaService = personaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<MatriculaDTOList>> listar() {
        List<MatriculaDTOList> lista = service.list()
                .stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<MatriculaDTOList> buscarId(@PathVariable Long id) {
        Matricula matricula = service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la matrícula: " + id));
        return ResponseEntity.ok(toDTO(matricula));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<MatriculaDTOList> registrar(@Valid @RequestBody MatriculaDTOInsert dto) {
        Matricula matricula = toEntity(dto, null);
        matricula.setIdMatricula(null);
        service.insert(matricula);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(matricula.getIdMatricula())
                .toUri();
        return ResponseEntity.created(location).body(toDTO(matricula));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<MatriculaDTOList> modificar(@PathVariable Long id, @Valid @RequestBody MatriculaDTOInsert dto) {
        service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la matrícula: " + id));
        Matricula matricula = toEntity(dto, id);
        matricula.setIdMatricula(id);
        service.update(matricula);
        return ResponseEntity.ok(toDTO(matricula));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Matricula matricula = service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la matrícula: " + id));
        service.delete(matricula.getIdMatricula());
        return ResponseEntity.noContent().build();
    }

    // Reporte 1 (colegios + matriculas): matrículas por colegio y su peso en el total.
    @GetMapping("/reportes/por-colegio")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<MatriculasPorColegioDTO>> matriculasPorColegio() {
        List<MatriculasPorColegioDTO> lista = service.matriculasPorColegio()
                .stream()
                .map(fila -> {
                    MatriculasPorColegioDTO dto = new MatriculasPorColegioDTO();
                    dto.setColegio((String) fila[0]);
                    dto.setDepartamento((String) fila[1]);
                    dto.setTipoZona((String) fila[2]);
                    dto.setTotalMatriculas(aLong(fila[3]));
                    dto.setPorcentajeDelTotal(aDouble(fila[4]));
                    return dto;
                })
                .toList();
        return ResponseEntity.ok(lista);
    }

    // Reporte 2 (persona + matriculas): estudiantes con más de una matrícula (posibles duplicados o traslados).
    @GetMapping("/reportes/estudiantes-varias-matriculas")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<EstudianteVariasMatriculasDTO>> estudiantesConVariasMatriculas() {
        List<EstudianteVariasMatriculasDTO> lista = service.estudiantesConVariasMatriculas()
                .stream()
                .map(fila -> {
                    EstudianteVariasMatriculasDTO dto = new EstudianteVariasMatriculasDTO();
                    dto.setIdPersona(aLong(fila[0]));
                    dto.setNombres((String) fila[1]);
                    dto.setApellidos((String) fila[2]);
                    dto.setMatriculas(aLong(fila[3]));
                    dto.setColegios(aLong(fila[4]));
                    return dto;
                })
                .toList();
        return ResponseEntity.ok(lista);
    }

    private MatriculaDTOList toDTO(Matricula matricula) {
        MatriculaDTOList dto = new MatriculaDTOList();
        dto.setIdMatricula(matricula.getIdMatricula());
        dto.setIdColegio(matricula.getColegio().getIdColegio());
        dto.setNombreColegio(matricula.getColegio().getNombre());
        dto.setIdPersona(matricula.getPersona().getIdPersona());
        return dto;
    }

    // HU42: solo se matricula a estudiantes. HU34: un estudiante no se matricula dos veces en el mismo colegio.
    private Matricula toEntity(MatriculaDTOInsert dto, Long idActual) {
        Matricula matricula = new Matricula();
        Colegio colegio = colegioService.listId(dto.getIdColegio())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el colegio: " + dto.getIdColegio()));
        Persona persona = personaRepository.findById(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la persona: " + dto.getIdPersona()));
        if (!personaService.tieneAlgunRol(persona, "ESTUDIANTE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se matricula a personas con rol ESTUDIANTE");
        }
        if (service.contarDuplicadas(dto.getIdPersona(), dto.getIdColegio(), idActual == null ? -1L : idActual) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El estudiante ya está matriculado en ese colegio");
        }
        matricula.setColegio(colegio);
        matricula.setPersona(persona);
        return matricula;
    }

    private static Long aLong(Object valor) {
        return valor == null ? null : ((Number) valor).longValue();
    }

    private static Double aDouble(Object valor) {
        return valor == null ? null : ((Number) valor).doubleValue();
    }
}
