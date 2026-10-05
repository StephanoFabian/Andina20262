package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.PerfilAcademicoDTO;
import pe.edu.upc.demosm2.entities.PerfilAcademico;
import pe.edu.upc.demosm2.serviceinterfaces.IPerfilAcademicoService;
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
@RequestMapping("/perfiles-academicos")
public class PerfilAcademicoController {

    @Autowired
    private IPerfilAcademicoService paS;

    @GetMapping
    public ResponseEntity<List<PerfilAcademicoDTO>> listar() {
        ModelMapper m = new ModelMapper();
        List<PerfilAcademicoDTO> lista = paS.list().stream()
                .map(x -> m.map(x, PerfilAcademicoDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<PerfilAcademicoDTO> registrar(@RequestBody PerfilAcademicoDTO dto) {
        ModelMapper m = new ModelMapper();
        PerfilAcademico x = m.map(dto, PerfilAcademico.class);
        PerfilAcademico srv = paS.insert(x);
        return ResponseEntity.status(HttpStatus.CREATED).body(m.map(srv, PerfilAcademicoDTO.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        ModelMapper m = new ModelMapper();
        Optional<PerfilAcademico> x = paS.listId(id);
        if (x.isPresent()) {
            return ResponseEntity.ok(m.map(x.get(), PerfilAcademicoDTO.class));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil académico no encontrado");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> actualizar(@RequestBody PerfilAcademicoDTO dto) {
        ModelMapper m = new ModelMapper();
        PerfilAcademico x = m.map(dto, PerfilAcademico.class);
        paS.update(x);
        return ResponseEntity.ok(m.map(x, PerfilAcademicoDTO.class));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        Optional<PerfilAcademico> x = paS.listId(id);
        if (x.isPresent()) {
            paS.delete(id);
            return ResponseEntity.ok("Perfil académico eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil académico no encontrado");
        }
    }

    // Query nativo: perfiles con nota menor a la indicada (alumnos a los que apoyar)
    @GetMapping("/nota-menor/{nota}")
    public ResponseEntity<List<PerfilAcademicoDTO>> listarPerfilesConNotaMenorA(@PathVariable Double nota) {
        ModelMapper m = new ModelMapper();
        List<PerfilAcademicoDTO> lista = paS.listarPerfilesConNotaMenorA(nota).stream()
                .map(x -> m.map(x, PerfilAcademicoDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }
}
