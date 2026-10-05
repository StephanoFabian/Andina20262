package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.MaterialDTO;
import pe.edu.upc.demosm2.dtos.MaterialPorTipoDTO;
import pe.edu.upc.demosm2.entities.Material;
import pe.edu.upc.demosm2.entities.Persona;
import pe.edu.upc.demosm2.serviceinterfaces.IMaterialService;
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
@RequestMapping("/materiales")
public class MaterialController {

    @Autowired
    private IMaterialService mS;

    @GetMapping
    public ResponseEntity<List<MaterialDTO>> listar() {
        ModelMapper m = new ModelMapper();
        List<MaterialDTO> lista = mS.list().stream()
                .map(x -> m.map(x, MaterialDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> registrar(@RequestBody MaterialDTO dto) {
        try {
            ModelMapper m = new ModelMapper();
            Material x = m.map(dto, Material.class);
            Persona p = new Persona();
            p.setIdPersona(dto.getIdPersona());
            x.setPersona(p);
            Material srv = mS.insert(x);
            return ResponseEntity.status(HttpStatus.CREATED).body(m.map(srv, MaterialDTO.class));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: La persona (" + dto.getIdPersona() + ") no existe o faltan datos.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        ModelMapper m = new ModelMapper();
        Optional<Material> x = mS.listId(id);
        if (x.isPresent()) {
            return ResponseEntity.ok(m.map(x.get(), MaterialDTO.class));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Material no encontrado");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<?> actualizar(@RequestBody MaterialDTO dto) {
        try {
            ModelMapper m = new ModelMapper();
            Material x = m.map(dto, Material.class);
            Persona p = new Persona();
            p.setIdPersona(dto.getIdPersona());
            x.setPersona(p);
            mS.update(x);
            return ResponseEntity.ok(m.map(x, MaterialDTO.class));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: La persona (" + dto.getIdPersona() + ") no existe o faltan datos.");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        Optional<Material> x = mS.listId(id);
        if (x.isPresent()) {
            mS.delete(id);
            return ResponseEntity.ok("Material eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Material no encontrado");
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<MaterialDTO>> buscarPorTitulo(@RequestParam String titulo) {
        ModelMapper m = new ModelMapper();
        List<MaterialDTO> lista = mS.buscarPorTitulo(titulo).stream()
                .map(x -> m.map(x, MaterialDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<MaterialDTO>> listarPorCurso(@PathVariable Long idCurso) {
        ModelMapper m = new ModelMapper();
        List<MaterialDTO> lista = mS.listarMaterialesPorCurso(idCurso).stream()
                .map(x -> m.map(x, MaterialDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // Query nativo: cuántos materiales hay de cada tipo
    @GetMapping("/reporte-por-tipo")
    public ResponseEntity<?> reporteMaterialesPorTipo() {
        List<Object[]> lista = mS.reporteMaterialesPorTipo();
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No hay materiales registrados para generar el reporte.");
        }
        List<MaterialPorTipoDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            MaterialPorTipoDTO dto = new MaterialPorTipoDTO();
            dto.setTipo((String) fila[0]);
            dto.setCantidad(((Number) fila[1]).longValue());
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }
}
