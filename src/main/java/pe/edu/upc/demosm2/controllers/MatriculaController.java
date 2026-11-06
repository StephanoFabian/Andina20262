package pe.edu.upc.demosm2.controllers;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.upc.demosm2.dtos.*;
import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.serviceinterfaces.MatriculaServiceInterface;
import pe.edu.upc.demosm2.repositories.IPersonaRepositories;
import pe.edu.upc.demosm2.serviceinterfaces.ColegioServiceInterface;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {
    private final MatriculaServiceInterface service;
    private final ColegioServiceInterface colegioService;
    private final IPersonaRepositories personaRepository;

    public MatriculaController(MatriculaServiceInterface service, ColegioServiceInterface colegioService, IPersonaRepositories personaRepository) {
        this.service = service;
        this.colegioService = colegioService;
        this.personaRepository = personaRepository;
    }

    @GetMapping
    public ResponseEntity<List<MatriculaDTOList>> listar() {
        List<MatriculaDTOList> lista = service.list()
                .stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatriculaDTOList> buscarId(@PathVariable Long id) {
        Matricula matricula = service.listId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la matrícula: " + id));
        return ResponseEntity.ok(toDTO(matricula));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<MatriculaDTOList> registrar(@Valid @RequestBody MatriculaDTOInsert dto) {
        Matricula matricula = toEntity(dto);
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
        Matricula matricula = toEntity(dto);
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

    private Matricula toEntity(MatriculaDTOInsert dto) {
        Matricula matricula = new Matricula();
        Colegio colegio = colegioService.listId(dto.getIdColegio())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el colegio: " + dto.getIdColegio()));
        Persona persona = personaRepository.findById(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la persona: " + dto.getIdPersona()));
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
    @GetMapping("/pagina")
    public pe.edu.upc.demosm2.dtos.PaginaDTO<pe.edu.upc.demosm2.dtos.MatriculaDTOList> pagina(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        if(page<0 || page>10000 || size<1 || size>100 || q.length()>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100; búsqueda: hasta 100 caracteres");
        String texto=q.trim().toLowerCase(java.util.Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        java.util.List<pe.edu.upc.demosm2.dtos.MatriculaDTOList> respuesta=new java.util.ArrayList<>();
        for(Object[] fila:service.pagina(texto,size,page*size)) respuesta.add(filaPagina(fila));
        return new pe.edu.upc.demosm2.dtos.PaginaDTO<>(respuesta,service.contarPagina(texto),page,size);
    }
    private pe.edu.upc.demosm2.dtos.MatriculaDTOList filaPagina(Object[] fila) {
        pe.edu.upc.demosm2.dtos.MatriculaDTOList dto=new pe.edu.upc.demosm2.dtos.MatriculaDTOList();
        dto.setIdMatricula((fila[0] == null ? null : ((Number)fila[0]).longValue()));
        dto.setIdColegio((fila[1] == null ? null : ((Number)fila[1]).longValue()));
        dto.setNombreColegio((fila[2] == null ? null : (String)fila[2]));
        dto.setIdPersona((fila[3] == null ? null : ((Number)fila[3]).longValue()));
        return dto;
    }
}
