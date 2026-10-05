package pe.edu.upc.demosm2.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.upc.demosm2.dtos.*;
import pe.edu.upc.demosm2.entities.Colegio;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.serviceinterfaces.ColegioServiceInterface;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/colegios")
public class ColegioController {
    private final ColegioServiceInterface service;
    private final ModelMapper mapper;

    public ColegioController(ColegioServiceInterface service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE','ESTUDIANTE')")
    @Operation(summary = "Listar colegios por páginas", description = "Arreglo ordenado por ID. X-Has-Next indica si hay otra página; size admite entre 1 y 100.")
    public ResponseEntity<List<ColegioDTOList>> listar(
            @RequestParam(defaultValue = "0") @Min(0) @Max(10000) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        Slice<ColegioDTOList> result = service.list(PageRequest.of(page, size, Sort.by("idColegio")))
                .map(colegio -> mapper.map(colegio, ColegioDTOList.class));
        return ResponseEntity.ok().header("X-Has-Next", String.valueOf(result.hasNext()))
                .header("X-Page", String.valueOf(page)).header("X-Page-Size", String.valueOf(size))
                .body(result.getContent());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE','ESTUDIANTE')")
    public ColegioDTOList buscar(@PathVariable @Positive Long id) {
        return mapper.map(find(id), ColegioDTOList.class);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<ColegioDTOList> registrar(@Valid @RequestBody ColegioDTOInsert dto) {
        if (dto.getIdColegio() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID se genera al crear el colegio");
        Colegio colegio = mapper.map(dto, Colegio.class);
        service.insert(colegio);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(colegio.getIdColegio()).toUri();
        return ResponseEntity.created(location).body(mapper.map(colegio, ColegioDTOList.class));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ColegioDTOList actualizar(@PathVariable @Positive Long id, @Valid @RequestBody ColegioDTOInsert dto) {
        if (dto.getIdColegio() != null && !id.equals(dto.getIdColegio()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID del cuerpo debe coincidir con la ruta");
        Colegio colegio = find(id);
        colegio.setNombre(dto.getNombre());
        colegio.setDepartamento(dto.getDepartamento());
        colegio.setProvincia(dto.getProvincia());
        colegio.setDistrito(dto.getDistrito());
        colegio.setComunidad(dto.getComunidad());
        colegio.setTipo_zona(dto.getTipo_zona());
        service.insert(colegio);
        return mapper.map(colegio, ColegioDTOList.class);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    @Operation(summary = "Eliminar un colegio", description = "Devuelve 409 si existen aulas, matrículas o asignaciones que dependen del colegio.")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        service.delete(find(id).getIdColegio());
        return ResponseEntity.noContent().build();
    }

    // HU06: conectividad de la escuela (ancho de banda y tipo de conexión) y si alcanza el mínimo para clases virtuales
    @GetMapping("/{id}/conectividad")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ConectividadDTO consultarConectividad(@PathVariable @Positive Long id) {
        return conectividadDTO(find(id));
    }

    @PutMapping("/{id}/conectividad")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    @Operation(summary = "Registrar la medición de conectividad", description = "Las asignaciones no presenciales se bloquean en escuelas sin medición o bajo el mínimo configurado.")
    public ConectividadDTO registrarConectividad(@PathVariable @Positive Long id, @Valid @RequestBody ConectividadDTO dto) {
        Colegio colegio = find(id);
        colegio.setVelocidadBajadaMbps(dto.getVelocidadBajadaMbps());
        colegio.setVelocidadSubidaMbps(dto.getVelocidadSubidaMbps());
        colegio.setTipoConexion(dto.getTipoConexion().trim().toUpperCase());
        colegio.setFechaMedicionConectividad(dto.getFechaMedicion() == null ? LocalDate.now() : dto.getFechaMedicion());
        service.insert(colegio);
        return conectividadDTO(colegio);
    }

    // Query nativo (colegios + matriculas) para decidir a qué escuelas llevar primero mejoras de conectividad
    @GetMapping("/reportes/conectividad")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public List<ConectividadColegioDTO> reporteConectividad() {
        List<ConectividadColegioDTO> respuesta = new ArrayList<>();
        for (Object[] fila : service.reporteConectividad()) {
            ConectividadColegioDTO dto = new ConectividadColegioDTO();
            dto.setIdColegio(((Number) fila[0]).longValue());
            dto.setColegio((String) fila[1]);
            dto.setDepartamento((String) fila[2]);
            dto.setTipoZona((String) fila[3]);
            dto.setTipoConexion((String) fila[4]);
            dto.setVelocidadBajadaMbps(fila[5] == null ? null : ((Number) fila[5]).doubleValue());
            dto.setVelocidadSubidaMbps(fila[6] == null ? null : ((Number) fila[6]).doubleValue());
            dto.setEstudiantes(((Number) fila[7]).longValue());
            dto.setEstado((String) fila[8]);
            respuesta.add(dto);
        }
        return respuesta;
    }

    private ConectividadDTO conectividadDTO(Colegio colegio) {
        ConectividadDTO dto = new ConectividadDTO();
        dto.setIdColegio(colegio.getIdColegio());
        dto.setVelocidadBajadaMbps(colegio.getVelocidadBajadaMbps());
        dto.setVelocidadSubidaMbps(colegio.getVelocidadSubidaMbps());
        dto.setTipoConexion(colegio.getTipoConexion());
        dto.setFechaMedicion(colegio.getFechaMedicionConectividad());
        dto.setCumpleMinimo(service.cumpleConectividadMinima(colegio));
        dto.setBajadaMinimaMbps(service.getBajadaMinimaMbps());
        dto.setSubidaMinimaMbps(service.getSubidaMinimaMbps());
        return dto;
    }

    private Colegio find(Long id) {
        return service.listId(id).orElseThrow(() -> new ResourceNotFoundException("No existe el colegio: " + id));
    }
}
