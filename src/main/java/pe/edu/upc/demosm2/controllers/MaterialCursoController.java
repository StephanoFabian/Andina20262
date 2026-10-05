package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.CursoMaterialesDTO;
import pe.edu.upc.demosm2.dtos.MaterialCursoDTO;
import pe.edu.upc.demosm2.entities.Curso;
import pe.edu.upc.demosm2.entities.Material;
import pe.edu.upc.demosm2.entities.MaterialCurso;
import pe.edu.upc.demosm2.entities.MaterialCursoId;
import pe.edu.upc.demosm2.serviceinterfaces.ICursoService;
import pe.edu.upc.demosm2.serviceinterfaces.IMaterialCursoService;
import pe.edu.upc.demosm2.serviceinterfaces.IMaterialService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/materiales-cursos")
public class MaterialCursoController {

    @Autowired
    private IMaterialCursoService mcS;

    @Autowired
    private IMaterialService mS;

    @Autowired
    private ICursoService cS;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE','ESTUDIANTE')")
    public ResponseEntity<List<MaterialCursoDTO>> listar() {
        ModelMapper m = new ModelMapper();
        // Se mapea desde la llave compuesta (MaterialCursoId), que tiene justo idMaterial e idCurso
        List<MaterialCursoDTO> lista = mcS.list().stream()
                .map(x -> m.map(x.getId(), MaterialCursoDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // Query nativo (curso + material_curso) para decidir a qué cursos subir materiales primero
    // HU50: IDs de los cursos a los que está asociado un material
    @GetMapping("/material/{idMaterial}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL','DOCENTE','ESTUDIANTE')")
    public ResponseEntity<List<MaterialCursoDTO>> listarPorMaterial(@PathVariable Long idMaterial) {
        ModelMapper m = new ModelMapper();
        List<MaterialCursoDTO> lista = mcS.listarPorMaterial(idMaterial).stream()
                .map(x -> m.map(x.getId(), MaterialCursoDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/reporte-materiales-por-curso")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> reporteMaterialesPorCurso() {
        List<Object[]> lista = mcS.reporteMaterialesPorCurso();
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay cursos registrados para generar el reporte.");
        }
        List<CursoMaterialesDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            CursoMaterialesDTO dto = new CursoMaterialesDTO();
            dto.setCurso((String) fila[0]);
            dto.setCantidadMateriales(((Number) fila[1]).longValue());
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    // HU48: el material y el curso deben existir (404) y la asociación no se repite (409)
    public ResponseEntity<?> registrar(@RequestBody MaterialCursoDTO dto) {
        if (dto.getIdMaterial() == null || dto.getIdCurso() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("idMaterial e idCurso son obligatorios");
        }
        Optional<Material> material = mS.listId(dto.getIdMaterial());
        if (material.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El material (" + dto.getIdMaterial() + ") no existe");
        }
        Optional<Curso> curso = cS.listId(dto.getIdCurso());
        if (curso.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El curso (" + dto.getIdCurso() + ") no existe");
        }
        if (mcS.listId(new MaterialCursoId(dto.getIdMaterial(), dto.getIdCurso())).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ese material ya está asociado a ese curso");
        }
        mcS.insert(new MaterialCurso(material.get(), curso.get()));
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
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
