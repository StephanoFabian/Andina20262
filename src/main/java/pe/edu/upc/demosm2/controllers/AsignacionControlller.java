package pe.edu.upc.demosm2.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<AsignacionDTOInsert> registrar (@Valid @RequestBody AsignacionDTOInsert dto){
        Curso q = cs.listId(dto.getId_curso())
        .orElseThrow(()->
                new ResourceNotFoundException(
                        "No existe el Curso: "+dto.getId_curso()
                ));

        PeriodoAcademico pe=peS.listId(dto.getId_periodo())
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "No existe el Periodo Academico: "+dto.getId_curso()
                        ));

        Persona per= pS.listId(dto.getId_persona())
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "No existe el Periodo Academico: "+dto.getId_curso()
                        ));

       Colegio cole = coS.listId(dto.getId_colegio())
              .orElseThrow(()->
                      new ResourceNotFoundException(
                              "No existe el Periodo Academico: "+dto.getId_curso()
                      ));

        AsignacionDocente ag=modelMapper.map(dto, AsignacionDocente.class);
        ag.setId_asignacion(ag.getId_asignacion());
        ag.setCurso(q);
        ag.setPeriodoAcademico(pe);
        ag.setPersona(per);
        ag.setColegio(cole);
        aS.insert(ag);

        AsignacionDTOInsert registro = modelMapper.map(ag,AsignacionDTOInsert.class);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(ag.getId_asignacion())
                .toUri();
        return ResponseEntity.created(location).body(registro);
    }

    @GetMapping
    public ResponseEntity <List<AsignacionDTOList>> listar(){
        List<AsignacionDTOList> lista= aS.list()
                .stream()
                .map(docente -> modelMapper.map(docente, AsignacionDTOList.class))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsignacionDTOList> buscarasignacion(@PathVariable Long id){
       AsignacionDocente asignacionDocente = aS.listId(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "Docente no encontrado, intente de nuevo: "+ id
                        ));
        AsignacionDTOList respuesta = modelMapper.map(asignacionDocente,AsignacionDTOList.class);
        return ResponseEntity.ok(respuesta);
    }

@PutMapping
    public ResponseEntity<AsignacionDTOInsert> actualizar (@Valid @RequestBody AsignacionDTOInsert dto){
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

        AsignacionDocente ad= existente.get();
        ad.setHorassemanales(dto.getHorassemanales());
        ad.setModalidad(dto.getModalidad());
        ad.setId_aula(dto.getId_aula());
        ad.setCurso(qurso.get());
        ad.setPeriodoAcademico(periodo.get());
        ad.setPersona(persona.get());
        ad.setColegio(cole.get());

        aS.update(ad);

        AsignacionDTOInsert actualizado = modelMapper.map(ad, AsignacionDTOInsert.class);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        AsignacionDocente docente = aS.listId(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "Docente no encontrado, intente de nuevo: "+ id
                        ));
        aS.delete(docente.getId_asignacion());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/horarios")
    public ResponseEntity<List<AsignacionDTOList>> ObtenerRangoSegunHoras(@RequestParam Long min, @RequestParam Long max){
        List<AsignacionDTOList> filtro= aS.ObtenerPorRangoHoras(min,max)
                .stream()
                .map(docente -> modelMapper.map(docente, AsignacionDTOList.class))
                .toList();
        return ResponseEntity.ok(filtro);
    }
}
