package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.CursoMaterialesDTO;
import pe.edu.upc.demosm2.dtos.MaterialCursoDTO;
import pe.edu.upc.demosm2.entities.Curso;
import pe.edu.upc.demosm2.entities.Material;
import pe.edu.upc.demosm2.entities.MaterialCurso;
import pe.edu.upc.demosm2.entities.MaterialCursoId;
import pe.edu.upc.demosm2.serviceinterfaces.IMaterialCursoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
    @GetMapping("/pagina")
    public pe.edu.upc.demosm2.dtos.PaginaDTO<pe.edu.upc.demosm2.dtos.MaterialCursoDTO> pagina(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q, @RequestParam(defaultValue="0") Long idMaterial) {
        if(page<0 || page>10000 || size<1 || size>100 || q.length()>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100; búsqueda: hasta 100 caracteres");
        String texto=q.trim().toLowerCase(java.util.Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        java.util.List<pe.edu.upc.demosm2.dtos.MaterialCursoDTO> respuesta=new java.util.ArrayList<>();
        for(Object[] fila:mcS.pagina(texto,size,page*size, idMaterial)) respuesta.add(filaPagina(fila));
        return new pe.edu.upc.demosm2.dtos.PaginaDTO<>(respuesta,mcS.contarPagina(texto, idMaterial),page,size);
    }
    private pe.edu.upc.demosm2.dtos.MaterialCursoDTO filaPagina(Object[] fila) {
        pe.edu.upc.demosm2.dtos.MaterialCursoDTO dto=new pe.edu.upc.demosm2.dtos.MaterialCursoDTO();
        dto.setIdMaterial((fila[0] == null ? null : ((Number)fila[0]).longValue()));
        dto.setIdCurso((fila[1] == null ? null : ((Number)fila[1]).longValue()));
        dto.setMaterial((fila[2] == null ? null : (String)fila[2]));
        dto.setCurso((fila[3] == null ? null : (String)fila[3]));
        return dto;
    }
    @GetMapping("/reporte-materiales-por-curso/pagina")
    public pe.edu.upc.demosm2.dtos.PaginaDTO<CursoMaterialesDTO> cursosPagina(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size) {
        if(page<0 || page>10000 || size<1 || size>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100");
        java.util.List<CursoMaterialesDTO> resultado=new java.util.ArrayList<>();
        for(Object[] fila:mcS.cursosPagina(size,page*size)) {CursoMaterialesDTO dto=new CursoMaterialesDTO();dto.setCurso((String)fila[0]);dto.setCantidadMateriales(((Number)fila[1]).longValue());resultado.add(dto);}
        return new pe.edu.upc.demosm2.dtos.PaginaDTO<>(resultado,mcS.contarCursos(),page,size);
    }
}
