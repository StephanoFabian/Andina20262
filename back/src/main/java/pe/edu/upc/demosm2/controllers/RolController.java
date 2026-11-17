package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.RolDTO;
import pe.edu.upc.demosm2.dtos.RolCantidadDTO;
import pe.edu.upc.demosm2.entities.Rol;
import pe.edu.upc.demosm2.serviceinterfaces.IRolService;
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

    // Query nativo (rol + persona) para decidir si falta personal de algún tipo
    @GetMapping("/reporte-cantidad-personas")
    public ResponseEntity<?> reporteCantidadPersonasPorRol() {
        List<Object[]> lista = rS.reporteCantidadPersonasPorRol();
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay roles registrados para generar el reporte.");
        }
        List<RolCantidadDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            RolCantidadDTO dto = new RolCantidadDTO();
            dto.setRol((String) fila[0]);
            dto.setCantidad(((Number) fila[1]).longValue());
            dto.setPorcentaje(fila[2] == null ? null : ((Number) fila[2]).doubleValue());
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }
    @GetMapping("/pagina")
    public pe.edu.upc.demosm2.dtos.PaginaDTO<pe.edu.upc.demosm2.dtos.RolDTO> pagina(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        if(page<0 || page>10000 || size<1 || size>100 || q.length()>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100; búsqueda: hasta 100 caracteres");
        String texto=q.trim().toLowerCase(java.util.Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        java.util.List<pe.edu.upc.demosm2.dtos.RolDTO> respuesta=new java.util.ArrayList<>();
        for(Object[] fila:rS.pagina(texto,size,page*size)) respuesta.add(filaPagina(fila));
        return new pe.edu.upc.demosm2.dtos.PaginaDTO<>(respuesta,rS.contarPagina(texto),page,size);
    }
    private pe.edu.upc.demosm2.dtos.RolDTO filaPagina(Object[] fila) {
        pe.edu.upc.demosm2.dtos.RolDTO dto=new pe.edu.upc.demosm2.dtos.RolDTO();
        dto.setIdTipoPersona((fila[0] == null ? null : ((Number)fila[0]).longValue()));
        dto.setDetalle((fila[1] == null ? null : (String)fila[1]));
        return dto;
    }
}
