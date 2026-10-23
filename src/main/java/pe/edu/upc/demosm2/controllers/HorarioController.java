package pe.edu.upc.demosm2.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.upc.demosm2.dtos.HorarioInsertDTO;
import pe.edu.upc.demosm2.dtos.HorarioListDTO;
import pe.edu.upc.demosm2.entities.Asignacion;
import pe.edu.upc.demosm2.entities.Horario;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
import pe.edu.upc.demosm2.serviceinterfaces.IAsignacionDocenteService;
import pe.edu.upc.demosm2.serviceinterfaces.IHorarioService;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/horario")
public class HorarioController {
    public final ModelMapper modelMapper;
    public final IHorarioService hS;
    public final IAsignacionDocenteService aS;

    public HorarioController(ModelMapper modelMapper, IHorarioService hS, IAsignacionDocenteService aS) {
        this.modelMapper = modelMapper;
        this.hS = hS;
        this.aS = aS;
    }

    @PostMapping
    public ResponseEntity<HorarioInsertDTO> registrar(@Valid @RequestBody HorarioInsertDTO dto){
        Asignacion a = aS.listId(dto.getIdAsignacion())
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "No existe la asignación: "+dto.getIdAsignacion()
                        ));

        Horario h= modelMapper.map(dto, Horario.class);
        hS.insert(h);

        HorarioInsertDTO registro = modelMapper.map(h, HorarioInsertDTO.class);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(h.getId_horario())
                .toUri();
        return ResponseEntity.created(location).body(registro);
    }


    @GetMapping
    public ResponseEntity <List<HorarioListDTO>> listar(){
List<HorarioListDTO> lista= hS.list()
        .stream()
        .map(horario -> modelMapper.map(horario,HorarioListDTO.class))
        .toList();
return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
public ResponseEntity<HorarioListDTO> buscarhorario (@PathVariable Long id){
Horario horario = hS.listId(id)
        .orElseThrow(()->
                new ResourceNotFoundException(
                        "Horario no encontrado o no existente: "+ id
                ));
HorarioListDTO respuesta= modelMapper.map(horario, HorarioListDTO.class);
return ResponseEntity.ok(respuesta);
    }

@PutMapping
public ResponseEntity<HorarioInsertDTO> actualizar(@Valid @RequestBody HorarioInsertDTO dto){
        Optional<Horario> existente = hS.listId(dto.getId_horario());
        if(existente.isEmpty()){
throw new ResourceNotFoundException("No existe un horario con id: " + dto.getId_horario()
);
        }
Optional<Asignacion> asignado = aS.listId(dto.getIdAsignacion());
        if(asignado.isEmpty()){
            throw new ResourceNotFoundException( "No existe la asignación con id: " + dto.getIdAsignacion());
        }

Horario h =existente.get();
h.setDiaSemana(dto.getDiaSemana());
h.setHoraInicio(dto.getHoraInicio());
h.setHoraFin(dto.getHoraFin());
h.setAsignacion(asignado.get());
    hS.update(h);
    HorarioInsertDTO actualizado = modelMapper.map(h, HorarioInsertDTO.class);
    return ResponseEntity.ok(actualizado);
}

@DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
Horario horario= hS.listId(id)
        .orElseThrow(()->
                new ResourceNotFoundException(
                        "Horario no encontrado o no existente: "+ id
                ));

    hS.delete(horario.getId_horario());
return ResponseEntity.noContent().build();
}

@GetMapping("/individual/{idPersona}")
    public ResponseEntity<List<HorarioListDTO>> listarPorPersona(@PathVariable Long idPersona){
        List<Horario> lista = hS.obtenerHorarioPorPersona(idPersona);

    List<HorarioListDTO> completo = lista.stream()
            .map(horario -> modelMapper.map(horario, HorarioListDTO.class))
            .collect(Collectors.toList());

        return ResponseEntity.ok(completo);
 }

}
