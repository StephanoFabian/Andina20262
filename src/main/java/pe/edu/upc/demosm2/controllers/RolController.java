package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.RolDTO;
import pe.edu.upc.demosm2.entities.Rol;
import pe.edu.upc.demosm2.servicesinterfaces.IRolService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roles")
public class RolController {

    @Autowired
    private IRolService rS;

    @GetMapping
    public ResponseEntity<List<RolDTO>> listar() {
        ModelMapper m = new ModelMapper();
        List<RolDTO> lista = rS.list().stream()
                .map(x -> m.map(x, RolDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RolDTO> registrar(@RequestBody RolDTO dto) {
        ModelMapper m = new ModelMapper();
        Rol x = m.map(dto, Rol.class);
        Rol srv = rS.insert(x);
        return ResponseEntity.status(HttpStatus.CREATED).body(m.map(srv, RolDTO.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        ModelMapper m = new ModelMapper();
        Optional<Rol> x = rS.listId(id);
        if (x.isPresent()) {
            return ResponseEntity.ok(m.map(x.get(), RolDTO.class));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizar(@RequestBody RolDTO dto) {
        ModelMapper m = new ModelMapper();
        Rol x = m.map(dto, Rol.class);
        rS.update(x);
        return ResponseEntity.ok(m.map(x, RolDTO.class));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        Optional<Rol> x = rS.listId(id);
        if (x.isPresent()) {
            rS.delete(id);
            return ResponseEntity.ok("Rol eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado");
        }
    }
}
