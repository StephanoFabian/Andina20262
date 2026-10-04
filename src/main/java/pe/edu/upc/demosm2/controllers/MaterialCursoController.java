package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.CursoMaterialesDTO;
import pe.edu.upc.demosm2.dtos.MaterialCursoDTO;
import pe.edu.upc.demosm2.entities.Curso;
import pe.edu.upc.demosm2.entities.Material;
import pe.edu.upc.demosm2.entities.MaterialCurso;
import pe.edu.upc.demosm2.entities.MaterialCursoId;
import pe.edu.upc.demosm2.servicesinterfaces.IMaterialCursoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/materiales-cursos")
public class MaterialCursoController {

    @Autowired
    private IMaterialCursoService mcS;

    @GetMapping
    public ResponseEntity<List<MaterialCursoDTO>> listar() {
        ModelMapper m = new ModelMapper();
        // Se mapea desde la llave compuesta (MaterialCursoId), que tiene justo idMaterial e idCurso
        List<MaterialCursoDTO> lista = mcS.list().stream()
                .map(x -> m.map(x.getId(), MaterialCursoDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // Query nativo (curso + material_curso) para decidir a qué cursos subir materiales primero
    @GetMapping("/reporte-materiales-por-curso")
    public ResponseEntity<?> reporteMaterialesPorCurso() {
        ModelMapper m = new ModelMapper();
        List<CursoMaterialesDTO> lista = mcS.reporteMaterialesPorCurso().stream()
                .map(x -> m.map(x, CursoMaterialesDTO.class))
                .collect(Collectors.toList());
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay cursos registrados para generar el reporte.");
        }
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> registrar(@RequestBody MaterialCursoDTO dto) {
        try {
            Material material = new Material();
            material.setIdMaterial(dto.getIdMaterial());
            Curso curso = new Curso();
            curso.setId_curso(dto.getIdCurso());
            mcS.insert(new MaterialCurso(material, curso));
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: El material (" + dto.getIdMaterial() + ") o el curso (" + dto.getIdCurso() + ") no existe.");
        }
    }

    @DeleteMapping("/{idMaterial}/{idCurso}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<String> eliminar(@PathVariable Long idMaterial, @PathVariable Long idCurso) {
        MaterialCursoId id = new MaterialCursoId(idMaterial, idCurso);
        Optional<MaterialCurso> x = mcS.listId(id);
        if (x.isPresent()) {
            mcS.delete(id);
            return ResponseEntity.ok("Material quitado del curso correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ese material no está asignado a ese curso");
        }
    }
}
