package pe.edu.upc.demosm2.controllers;


import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import pe.edu.upc.demosm2.dtos.ColegioDTOInsert;
import pe.edu.upc.demosm2.dtos.ColegioDTOList;
import pe.edu.upc.demosm2.entities.Colegio;
import pe.edu.upc.demosm2.serviceinterfaces.ColegioServiceInterface;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/colegios")
public class ColegioController {


    private final ColegioServiceInterface CSI;
    private final ModelMapper MM;

    // generar constructor
    public ColegioController(ColegioServiceInterface CSI, ModelMapper MM) {
        this.CSI = CSI;
        this.MM = MM;
    }

    @GetMapping
    //utilizar el dtos
    public ResponseEntity<List<ColegioDTOList>> listar(){
        List<ColegioDTOList> lista =CSI.list()
                .stream()
                .map(colegio->MM.map(colegio,ColegioDTOList.class))
                .toList();

        return ResponseEntity.ok(lista);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<ColegioDTOInsert> registrar(
            @Valid @RequestBody ColegioDTOInsert dto) {
        Colegio st = MM.map(dto, Colegio.class);
        st.setIdColegio(null);
        CSI.insert(st);
        ColegioDTOInsert responseDTO =
                MM.map(st, ColegioDTOInsert.class);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(st.getIdColegio())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(responseDTO);
    }

    @GetMapping("/{id}")
    public ColegioDTOList buscar(@PathVariable Long id) { return MM.map(existente(id),ColegioDTOList.class); }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ColegioDTOList actualizar(@PathVariable Long id,@Valid @RequestBody ColegioDTOInsert dto) {
        existente(id);
        Colegio colegio=MM.map(dto,Colegio.class); colegio.setIdColegio(id); CSI.insert(colegio);
        return MM.map(colegio,ColegioDTOList.class);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { existente(id); CSI.delete(id); return ResponseEntity.noContent().build(); }
    private Colegio existente(Long id) { return CSI.listId(id).orElseThrow(() -> new pe.edu.upc.demosm2.exceptions.ResourceNotFoundException("No existe el colegio: " + id)); }
}
