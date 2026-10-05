package pe.edu.upc.demosm2.controllers;


import jakarta.validation.Valid;
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
    public ResponseEntity<ColegioDTOInsert> registrar(
            @Valid @RequestBody ColegioDTOInsert dto) {
        Colegio st = MM.map(dto, Colegio.class);
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

}
