package pe.edu.upc.demosm2.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.upc.demosm2.dtos.AsignacionDTOInsert;
import pe.edu.upc.demosm2.dtos.AsignacionDTOList;
import pe.edu.upc.demosm2.entities.AsignacionDocente;
import pe.edu.upc.demosm2.entities.Colegio;
import pe.edu.upc.demosm2.entities.Curso;
import pe.edu.upc.demosm2.entities.PeriodoAcademico;
import pe.edu.upc.demosm2.entities.Persona;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.serviceinterfaces.IAsignacionDocenteService;
import pe.edu.upc.demosm2.serviceinterfaces.ICursoService;
import pe.edu.upc.demosm2.serviceinterfaces.IPersonaService;
import pe.edu.upc.demosm2.serviceinterfaces.PeriodoAcademicoServiceInterface;
import pe.edu.upc.demosm2.serviceimplements.ColegioServiceImplement;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/asignacion")
public class AsignacionControlller {
    public final IAsignacionDocenteService aS;
    public final ICursoService cs;
    public final ColegioServiceImplement coS;
    public final PeriodoAcademicoServiceInterface peS;
    public final IPersonaService pS;
    public final ModelMapper modelMapper;

    public AsignacionControlller(IAsignacionDocenteService aS, ICursoService cs, ColegioServiceImplement coS,
                                 PeriodoAcademicoServiceInterface peS, IPersonaService pS, ModelMapper modelMapper) {
        this.aS = aS;
        this.cs = cs;
        this.coS = coS;
        this.peS = peS;
        this.pS = pS;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<AsignacionDTOInsert> registrar (@Valid @RequestBody AsignacionDTOInsert dto){
        Curso q = cs.listId(dto.getId_curso())
        .orElseThrow(()->
                new ResourceNotFoundException(
                        "No existe el Curso: "+dto.getId_curso()
                ));

        PeriodoAcademico pe=peS.listId(dto.getId_periodo())
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "No existe el Periodo Academico: "+dto.getId_periodo()
                        ));

        Persona per= pS.listId(dto.getId_persona())
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "No existe la persona: "+dto.getId_persona()
                        ));

       Colegio cole = coS.listId(dto.getId_colegio())
              .orElseThrow(()->
                      new ResourceNotFoundException(
                              "No existe el colegio: "+dto.getId_colegio()
                      ));

        validarAsignacion(dto, per, cole, null);
        AsignacionDocente ag=modelMapper.map(dto, AsignacionDocente.class);
        ag.setId_asignacion(null);
        ag.setCurso(q);
        ag.setPeriodoAcademico(pe);
        ag.setPersona(per);
        ag.setColegio(cole);
        aS.insert(ag);

        AsignacionDTOInsert registro = toInsertDTO(ag);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(ag.getId_asignacion())
                .toUri();
        return ResponseEntity.created(location).body(registro);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity <List<AsignacionDTOList>> listar(){
        List<AsignacionDTOList> lista= aS.list()
                .stream()
                .map(this::toListDTO)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<AsignacionDTOList> buscarasignacion(@PathVariable Long id){
       AsignacionDocente asignacionDocente = aS.listId(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "Docente no encontrado, intente de nuevo: "+ id
                        ));
        AsignacionDTOList respuesta = toListDTO(asignacionDocente);
        return ResponseEntity.ok(respuesta);
    }

@PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<AsignacionDTOInsert> actualizar (@Valid @RequestBody AsignacionDTOInsert dto){
        if(dto.getId_asignacion()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id_asignacion es obligatorio para actualizar");
        }
        Optional<AsignacionDocente> existente =aS.listId(dto.getId_asignacion());
        if(existente.isEmpty()){
            throw new ResourceNotFoundException( "No existe la asignación con id: " + dto.getId_asignacion()
            );
        }

        Optional<Curso> qurso =cs.listId(dto.getId_curso());
        if(qurso.isEmpty()){
            throw new ResourceNotFoundException( "No existe el curso con id: " + dto.getId_curso()
            );
        }

        Optional<PeriodoAcademico> periodo = peS.listId(dto.getId_periodo());
        if(periodo.isEmpty()){
            throw new ResourceNotFoundException( "No existe el periodo academico con id: " + dto.getId_periodo()
            );
        }

        Optional<Persona> persona = pS.listId(dto.getId_persona());
        if(persona.isEmpty()){
            throw new ResourceNotFoundException( "No existe una persona con id: " + dto.getId_persona()
            );
        }

        Optional<Colegio> cole = coS.listId(dto.getId_colegio());
        if(cole.isEmpty()){
            throw new ResourceNotFoundException( "No existe un colegio con id: " + dto.getId_colegio()
            );
        }

        validarAsignacion(dto, persona.get(), cole.get(), dto.getId_asignacion());
        AsignacionDocente ad= existente.get();
        ad.setHorassemanales(dto.getHorassemanales());
        ad.setModalidad(dto.getModalidad());
        ad.setId_aula(dto.getId_aula());
        ad.setCurso(qurso.get());
        ad.setPeriodoAcademico(periodo.get());
        ad.setPersona(persona.get());
        ad.setColegio(cole.get());

        aS.update(ad);

        AsignacionDTOInsert actualizado = toInsertDTO(ad);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        AsignacionDocente docente = aS.listId(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "Docente no encontrado, intente de nuevo: "+ id
                        ));
        aS.delete(docente.getId_asignacion());
        return ResponseEntity.noContent().build();
    }

    // HU59: asignaciones dentro del rango de horas, ordenadas por ID ascendente; el rango se valida
    @GetMapping("/horarios")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<List<AsignacionDTOList>> ObtenerRangoSegunHoras(@RequestParam Long min, @RequestParam Long max){
        if(min < 0 || max < min){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rango de horas inválido: min debe ser >= 0 y max >= min");
        }
        List<AsignacionDTOList> filtro= aS.ObtenerPorRangoHoras(min,max)
                .stream()
                .map(this::toListDTO)
                .toList();
        return ResponseEntity.ok(filtro);
    }

    // HU07: la persona debe ser docente, el curso no puede tener ya docente en esa aula, colegio y periodo,
    // y las clases no presenciales exigen la conectividad mínima del colegio (HU06)
    private void validarAsignacion(AsignacionDTOInsert dto, Persona persona, Colegio colegio, Long idActual){
        if(!pS.tieneAlgunRol(persona, "DOCENTE", "ESPECIALISTA", "LOCAL")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se asigna a personas con rol DOCENTE, ESPECIALISTA o LOCAL");
        }
        if(dto.getHorassemanales() <= 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las horas semanales deben ser mayores que cero");
        }
        long conflictos = aS.contarConflictos(colegio.getIdColegio(), dto.getId_curso(), dto.getId_periodo(),
                dto.getId_aula(), idActual == null ? -1L : idActual);
        if(conflictos > 0){
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Conflicto de horario: ese curso ya tiene docente en esa aula, colegio y periodo");
        }
        if(!"PRESENCIAL".equalsIgnoreCase(dto.getModalidad().trim()) && !coS.cumpleConectividadMinima(colegio)){
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El colegio no tiene la conectividad mínima para clases no presenciales ("
                            + coS.getBajadaMinimaMbps() + " Mbps de bajada y " + coS.getSubidaMinimaMbps() + " de subida)");
        }
    }

    private AsignacionDTOList toListDTO(AsignacionDocente a){
        AsignacionDTOList dto = new AsignacionDTOList();
        dto.setId_asignacion(a.getId_asignacion());
        dto.setId_aula(a.getId_aula());
        dto.setId_curso(a.getCurso() == null ? null : a.getCurso().getId_curso());
        dto.setModalidad(a.getModalidad());
        dto.setHorassemanales(a.getHorassemanales());
        dto.setId_persona(a.getPersona() == null ? null : a.getPersona().getIdPersona());
        dto.setId_periodo(a.getPeriodoAcademico() == null ? null : a.getPeriodoAcademico().getIdPeriodo());
        dto.setId_colegio(a.getColegio() == null ? null : a.getColegio().getIdColegio());
        return dto;
    }

    private AsignacionDTOInsert toInsertDTO(AsignacionDocente a){
        AsignacionDTOInsert dto = new AsignacionDTOInsert();
        dto.setId_asignacion(a.getId_asignacion());
        dto.setId_aula(a.getId_aula());
        dto.setId_curso(a.getCurso() == null ? null : a.getCurso().getId_curso());
        dto.setId_periodo(a.getPeriodoAcademico() == null ? null : a.getPeriodoAcademico().getIdPeriodo());
        dto.setId_persona(a.getPersona() == null ? null : a.getPersona().getIdPersona());
        dto.setModalidad(a.getModalidad());
        dto.setHorassemanales(a.getHorassemanales());
        dto.setId_colegio(a.getColegio() == null ? null : a.getColegio().getIdColegio());
        return dto;
    }
}
