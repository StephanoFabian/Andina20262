package pe.edu.upc.demosm2.controllers;

import java.security.Principal;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.demosm2.dtos.AccesoVideollamadaDTO;
import pe.edu.upc.demosm2.dtos.FragmentoTraduccionDTO;
import pe.edu.upc.demosm2.serviceinterfaces.IVideollamadaService;

/** El JWT de Andina se valida antes de acceder a cualquier sala o fragmento. */
@RestController
@RequestMapping("/api/sesiones-virtuales/{sesion}/videollamada")
public class VideollamadaController {
    private final IVideollamadaService service;
    public VideollamadaController(IVideollamadaService service) { this.service = service; }

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<pe.edu.upc.demosm2.dtos.ErrorResponse> error(org.springframework.web.server.ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(new pe.edu.upc.demosm2.dtos.ErrorResponse(
                ex.getStatusCode().value(), ex.getReason(), java.time.Instant.now().toString()));
    }

    @PostMapping("/acceso")
    public ResponseEntity<AccesoVideollamadaDTO> acceso(@PathVariable Long sesion, Principal usuario) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.acceso(sesion, usuario.getName()));
    }

    @PostMapping("/fragmentos")
    public ResponseEntity<FragmentoTraduccionDTO> enviar(@PathVariable Long sesion, Principal usuario,
                                                       @RequestBody FragmentoTraduccionDTO dto) {
        return ResponseEntity.accepted().cacheControl(CacheControl.noStore()).body(service.enviar(sesion, usuario.getName(), dto));
    }

    @GetMapping("/fragmentos/{fragmento}")
    public ResponseEntity<FragmentoTraduccionDTO> resultado(@PathVariable Long sesion, Principal usuario,
                                                          @PathVariable String fragmento) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.resultado(sesion, usuario.getName(), fragmento));
    }
}
