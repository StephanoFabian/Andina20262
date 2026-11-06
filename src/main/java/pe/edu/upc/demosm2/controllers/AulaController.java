package pe.edu.upc.demosm2.controllers;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import pe.edu.upc.demosm2.dtos.AulaDTOInsert;
import pe.edu.upc.demosm2.dtos.AulaDTOList;
import pe.edu.upc.demosm2.entities.Aula;
import pe.edu.upc.demosm2.entities.Colegio;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.serviceinterfaces.AulaServiceInterface;
import pe.edu.upc.demosm2.serviceinterfaces.ColegioServiceInterface;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/aula")
public class AulaController {

    private final AulaServiceInterface ASI;
    private final ModelMapper MM;
    private final ColegioServiceInterface CSI;

    public AulaController(AulaServiceInterface ASI, ModelMapper MM, ColegioServiceInterface CSI) {
        this.ASI = ASI;
        this.MM = MM;
        this.CSI = CSI;
    }

    @GetMapping
    public ResponseEntity<List<AulaDTOList>> listar(){
        List<AulaDTOList> lista=ASI.list()
                .stream()
                .map(m->MM.map(m,AulaDTOList.class))
                .toList();

        return ResponseEntity.ok(lista);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<AulaDTOInsert> registrar(@Valid @RequestBody AulaDTOInsert dto){
        Colegio st=CSI.listId(dto.getIdColegio())
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "No existe el Colegio"+dto.getIdColegio()
                        ));
        Aula mv=MM.map(dto, Aula.class);
        mv.setIdAula(null);
        mv.setColegio(st);
        ASI.insert(mv);
        AulaDTOInsert responseDTO=MM.map(mv,AulaDTOInsert.class);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(mv.getIdAula())
                .toUri();

        return ResponseEntity.created(location).body(responseDTO) ;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AulaDTOList>buscarid(@PathVariable Long id){
        Aula movie=ASI.listId(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "No existe el aula: "+id
                        ));
        AulaDTOList respondeDTO=MM.map(movie,AulaDTOList.class);
        return ResponseEntity.ok(respondeDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<Void>eliminar(@PathVariable Long id){
        Aula movie=ASI.listId(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "No existe el aula: "+id
                        ));
        ASI.delete(movie.getIdAula());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<AulaDTOList> actualizar(@PathVariable Long id, @Valid @RequestBody AulaDTOInsert dto) {
        Aula aula=ASI.listId(id).orElseThrow(() -> new ResourceNotFoundException("No existe el aula: " + id));
        Colegio colegio=CSI.listId(dto.getIdColegio()).orElseThrow(() -> new ResourceNotFoundException("No existe el colegio"));
        aula.setNombre(dto.getNombre()); aula.setSeccion(dto.getSeccion()); aula.setCapacidad(dto.getCapacidad()); aula.setColegio(colegio);
        ASI.insert(aula);
        return ResponseEntity.ok(MM.map(aula,AulaDTOList.class));
    }
}
