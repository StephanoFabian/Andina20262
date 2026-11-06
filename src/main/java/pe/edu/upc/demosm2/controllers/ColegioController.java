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
    @GetMapping("/pagina")
    public pe.edu.upc.demosm2.dtos.PaginaDTO<pe.edu.upc.demosm2.dtos.ColegioDTOList> pagina(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        if(page<0 || page>10000 || size<1 || size>100 || q.length()>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100; búsqueda: hasta 100 caracteres");
        String texto=q.trim().toLowerCase(java.util.Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        java.util.List<pe.edu.upc.demosm2.dtos.ColegioDTOList> respuesta=new java.util.ArrayList<>();
        for(Object[] fila:CSI.pagina(texto,size,page*size)) respuesta.add(filaPagina(fila));
        return new pe.edu.upc.demosm2.dtos.PaginaDTO<>(respuesta,CSI.contarPagina(texto),page,size);
    }
    private pe.edu.upc.demosm2.dtos.ColegioDTOList filaPagina(Object[] fila) {
        pe.edu.upc.demosm2.dtos.ColegioDTOList dto=new pe.edu.upc.demosm2.dtos.ColegioDTOList();
        dto.setIdColegio((fila[0] == null ? null : ((Number)fila[0]).longValue()));
        dto.setNombre((fila[1] == null ? null : (String)fila[1]));
        dto.setDepartamento((fila[2] == null ? null : (String)fila[2]));
        dto.setProvincia((fila[3] == null ? null : (String)fila[3]));
        dto.setDistrito((fila[4] == null ? null : (String)fila[4]));
        dto.setComunidad((fila[5] == null ? null : (String)fila[5]));
        dto.setTipo_zona((fila[6] == null ? null : (String)fila[6]));
        return dto;
    }
}
