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
    @GetMapping("/pagina")
    public pe.edu.upc.demosm2.dtos.PaginaDTO<pe.edu.upc.demosm2.dtos.MaterialDTO> pagina(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        if(page<0 || page>10000 || size<1 || size>100 || q.length()>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100; búsqueda: hasta 100 caracteres");
        String texto=q.trim().toLowerCase(java.util.Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        java.util.List<pe.edu.upc.demosm2.dtos.MaterialDTO> respuesta=new java.util.ArrayList<>();
        for(Object[] fila:mS.pagina(texto,size,page*size)) respuesta.add(filaPagina(fila));
        return new pe.edu.upc.demosm2.dtos.PaginaDTO<>(respuesta,mS.contarPagina(texto),page,size);
    }
    private pe.edu.upc.demosm2.dtos.MaterialDTO filaPagina(Object[] fila) {
        pe.edu.upc.demosm2.dtos.MaterialDTO dto=new pe.edu.upc.demosm2.dtos.MaterialDTO();
        dto.setIdMaterial((fila[0] == null ? null : ((Number)fila[0]).longValue()));
        dto.setTitulo((fila[1] == null ? null : (String)fila[1]));
        dto.setDescripcion((fila[2] == null ? null : (String)fila[2]));
        dto.setTipo((fila[3] == null ? null : (String)fila[3]));
        dto.setUrlArchivo((fila[4] == null ? null : (String)fila[4]));
        dto.setFechaPublicacion((fila[5] == null ? null : (fila[5] instanceof java.time.LocalDate ? (java.time.LocalDate)fila[5] : ((java.sql.Date)fila[5]).toLocalDate())));
        dto.setIdPersona((fila[6] == null ? null : ((Number)fila[6]).longValue()));
        dto.setNombreAutor((fila[7] == null ? null : (String)fila[7]));
        dto.setApellidosAutor((fila[8] == null ? null : (String)fila[8]));
        return dto;
    }
    @GetMapping("/tipos")
    public java.util.List<String> tipos(@RequestParam(defaultValue="") String q) {
        if(q.length()>100)throw new IllegalArgumentException("Búsqueda demasiado larga");
        String texto=q.trim().toLowerCase(java.util.Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        return mS.tiposSugeridos(texto);
    }
    @GetMapping("/consulta/pagina")
    public pe.edu.upc.demosm2.dtos.PaginaDTO<MaterialDTO> consultaPagina(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q,@RequestParam(defaultValue="0") Long idCurso) {
        if(page<0 || page>10000 || size<1 || size>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100");
        if(q.length()>100 || idCurso<0)throw new IllegalArgumentException("Revisa la búsqueda o el curso");
        String texto=q.trim().toLowerCase(java.util.Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        java.util.List<MaterialDTO> resultado=new java.util.ArrayList<>();
        for(Object[] fila:mS.consultaPagina(texto,idCurso,size,page*size)) {
            MaterialDTO dto=new MaterialDTO();dto.setIdMaterial(((Number)fila[0]).longValue());dto.setTitulo((String)fila[1]);dto.setDescripcion((String)fila[2]);dto.setTipo((String)fila[3]);dto.setUrlArchivo((String)fila[4]);dto.setFechaPublicacion(fila[5]==null?null:(fila[5] instanceof java.time.LocalDate ? (java.time.LocalDate)fila[5]:((java.sql.Date)fila[5]).toLocalDate()));dto.setIdPersona(((Number)fila[6]).longValue());resultado.add(dto);
        }
        return new pe.edu.upc.demosm2.dtos.PaginaDTO<>(resultado,mS.contarConsulta(texto,idCurso),page,size);
    }
    @GetMapping("/reporte-por-tipo/pagina")
    public pe.edu.upc.demosm2.dtos.PaginaDTO<pe.edu.upc.demosm2.dtos.MaterialPorTipoDTO> tiposPagina(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size) {
        if(page<0 || page>10000 || size<1 || size>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100");
        java.util.List<pe.edu.upc.demosm2.dtos.MaterialPorTipoDTO> resultado=new java.util.ArrayList<>();
        for(Object[] fila:mS.tiposPagina(size,page*size)) {
            pe.edu.upc.demosm2.dtos.MaterialPorTipoDTO dto=new pe.edu.upc.demosm2.dtos.MaterialPorTipoDTO();dto.setTipo((String)fila[0]);dto.setCantidad(((Number)fila[1]).longValue());resultado.add(dto);
        }
        return new pe.edu.upc.demosm2.dtos.PaginaDTO<>(resultado,mS.contarTipos(),page,size);
    }
}
