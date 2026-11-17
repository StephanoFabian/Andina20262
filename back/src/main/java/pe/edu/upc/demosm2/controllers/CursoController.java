package pe.edu.upc.demosm2.controllers;


import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
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
    public ResponseEntity <List<CursoDTOList>> listar(){
        List<CursoDTOList> lista= cS.list()
                .stream()
                .map(curso -> modelMapper.map(curso, CursoDTOList.class))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
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
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<CursoDTOInsert> actualizar(@Valid @RequestBody CursoDTOInsert dto){
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
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        Curso curso = cS.listId(id)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "No existe el curso, intente de nuevo: "+ id
                        ));
        cS.delete(curso.getId_curso());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/area")
    public ResponseEntity<List<CursoDTOList>> obtenercursosporarea(@RequestParam String a){
List<CursoDTOList> filtro = cS.buscarporArea(a)
        .stream()
        .map(curso -> modelMapper.map(curso,CursoDTOList.class))
        .toList();
return ResponseEntity.ok(filtro);
    }
    @GetMapping("/pagina")
    public pe.edu.upc.demosm2.dtos.PaginaDTO<pe.edu.upc.demosm2.dtos.CursoDTOList> pagina(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        if(page<0 || page>10000 || size<1 || size>100 || q.length()>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100; búsqueda: hasta 100 caracteres");
        String texto=q.trim().toLowerCase(java.util.Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        java.util.List<pe.edu.upc.demosm2.dtos.CursoDTOList> respuesta=new java.util.ArrayList<>();
        for(Object[] fila:cS.pagina(texto,size,page*size)) respuesta.add(filaPagina(fila));
        return new pe.edu.upc.demosm2.dtos.PaginaDTO<>(respuesta,cS.contarPagina(texto),page,size);
    }
    private pe.edu.upc.demosm2.dtos.CursoDTOList filaPagina(Object[] fila) {
        pe.edu.upc.demosm2.dtos.CursoDTOList dto=new pe.edu.upc.demosm2.dtos.CursoDTOList();
        dto.setId_curso((fila[0] == null ? null : ((Number)fila[0]).longValue()));
        dto.setNombre_curso((fila[1] == null ? null : (String)fila[1]));
        dto.setDescripcion((fila[2] == null ? null : (String)fila[2]));
        dto.setArea((fila[3] == null ? null : (String)fila[3]));
        return dto;
    }
}
