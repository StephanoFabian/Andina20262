package pe.edu.upc.demosm2.controllers;

import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upc.demosm2.dtos.*;
import pe.edu.upc.demosm2.serviceinterfaces.IMaterialIAService;

@RestController
@RequestMapping("/api/materiales-ia")
@PreAuthorize("hasAnyRole('ADMIN','ADMIN_ESCUELA','ESPECIALISTA','LOCAL')")
public class MaterialIAController {
    private final IMaterialIAService service;
    public MaterialIAController(IMaterialIAService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<MaterialIADTO> generar(Principal usuario, @Valid @RequestBody MaterialIADTO dto) {
        return ResponseEntity.accepted().cacheControl(CacheControl.noStore()).body(service.generar(usuario.getName(), dto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<MaterialIADTO> consultar(Principal usuario, @PathVariable String id) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.consultar(usuario.getName(), id));
    }
    @GetMapping("/{id}/archivos/{nombre}")
    public ResponseEntity<byte[]> archivo(Principal usuario, @PathVariable String id, @PathVariable String nombre) {
        byte[] bytes = service.archivo(usuario.getName(), id, nombre);
        MediaType tipo = nombre.endsWith(".pdf") ? MediaType.APPLICATION_PDF : nombre.endsWith(".png") ? MediaType.IMAGE_PNG :
                nombre.endsWith(".svg") ? MediaType.valueOf("image/svg+xml") : nombre.endsWith(".docx") ?
                MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document") : MediaType.TEXT_PLAIN;
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).contentType(tipo)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(nombre).build().toString())
                .header("X-Content-Type-Options", "nosniff").body(bytes);
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> error(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(new ErrorResponse(ex.getStatusCode().value(), ex.getReason(), java.time.Instant.now().toString()));
    }
}
