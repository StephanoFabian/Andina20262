package pe.edu.upc.demosm2.controllers;


import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.upc.demosm2.dtos.CursoDTOInsert;
import pe.edu.upc.demosm2.dtos.CursoDTOList;
import pe.edu.upc.demosm2.entities.Curso;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.serviceinterfaces.ICursoService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/curso")
public class CursoController {
    public final ICursoService cS;
    public final ModelMapper modelMapper;

    public CursoController(ICursoService cS, ModelMapper modelMapper) {
        this.cS = cS;
        this.modelMapper = modelMapper;
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA')")
    public ResponseEntity<CursoDTOInsert> resgitrar (@Valid @RequestBody CursoDTOInsert dto){
        Curso q = modelMapper.map(dto, Curso.class);
        q.setId_curso(null);
        cS.insert(q);
        CursoDTOInsert registro = modelMapper.map(q, CursoDTOInsert.class);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(q.getId_curso())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(registro);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE','ESTUDIANTE')")
    public ResponseEntity <List<CursoDTOList>> listar(){
        List<CursoDTOList> lista= cS.list()
                .stream()
                .map(curso -> modelMapper.map(curso, CursoDTOList.class))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE','ESTUDIANTE')")
    public ResponseEntity<CursoDTOList> buscarcurso(@PathVariable Long id){
        Curso curso = cS.listId(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "No existe el curso, intente de nuevo: "+ id
                        ));
        CursoDTOList respuesta = modelMapper.map(curso,CursoDTOList.class);
        return ResponseEntity.ok(respuesta);
    }

@PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA')")
    public ResponseEntity<CursoDTOInsert> actualizar(@Valid @RequestBody CursoDTOInsert dto){
        if(dto.getId_curso()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id_curso es obligatorio para actualizar");
        }
        Curso existente = cS.listId(dto.getId_curso())
                .orElseThrow(()->
                        new ResourceNotFoundException(
        "No existe el curso de ID:" + dto.getId_curso()
                                )
                        );
        Curso curso =modelMapper.map(dto,Curso.class);
        curso.setId_curso(existente.getId_curso());
                cS.update(curso);

        CursoDTOInsert actualizado =modelMapper.map(curso,CursoDTOInsert.class);
        return ResponseEntity.ok(actualizado);

    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        Curso curso = cS.listId(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "No existe el curso, intente de nuevo: "+ id
                        ));
        cS.delete(curso.getId_curso());
        return ResponseEntity.noContent().build();
    }

    // HU60: cursos de un área; si el área no tiene cursos se indica con un mensaje (404)
    @GetMapping("/area")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE','ESTUDIANTE')")
    public ResponseEntity<List<CursoDTOList>> obtenercursosporarea(@RequestParam String a){
if(a.isBlank()){
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica el área a consultar");
}
List<CursoDTOList> filtro = cS.buscarporArea(a.trim())
        .stream()
        .map(curso -> modelMapper.map(curso,CursoDTOList.class))
        .toList();
if(filtro.isEmpty()){
    throw new ResourceNotFoundException("No existe el área o no tiene cursos registrados: " + a.trim());
}
return ResponseEntity.ok(filtro);
    }
}
