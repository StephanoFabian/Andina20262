package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.AuditoriaDTO;
import pe.edu.upc.demosm2.dtos.LoginFallidoDTO;
import pe.edu.upc.demosm2.serviceinterfaces.IAuditoriaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// HU15: consulta de la bitácora de accesos y cambios. Solo lectura y solo para el ADMIN (auditor principal).
// Los registros los escriben el login y AuditoriaInterceptor; no hay endpoints para modificarlos ni borrarlos.
@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {
    @Autowired
    private IAuditoriaService auS;

    // Filtros opcionales por usuario, módulo, acción y rango de fechas (ISO, p. ej. 2026-10-05T00:00:00).
    // Por páginas (size de 1 a 100), del registro más reciente al más antiguo.
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listar(@RequestParam(required = false) String usuario,
                                    @RequestParam(required = false) String modulo,
                                    @RequestParam(required = false) String accion,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "50") int size) {
        if (page < 0 || page > 10000 || size < 1 || size > 100) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("page va de 0 a 10000 y size de 1 a 100");
        }
        ModelMapper m = new ModelMapper();
        List<AuditoriaDTO> lista = auS.filtrar(vacioANull(usuario), mayusculas(modulo), mayusculas(accion), desde, hasta, page, size)
                .stream()
                .map(x -> m.map(x, AuditoriaDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // Query nativo (auditoria) para decidir qué cuentas revisar o bloquear por intentos fallidos de acceso
    @GetMapping("/reporte-login-fallidos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reporteLoginFallidos(@RequestParam(defaultValue = "7") Integer dias) {
        if (dias < 1 || dias > 365) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("dias va de 1 a 365");
        }
        List<Object[]> lista = auS.reporteLoginFallidos(dias);
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay intentos fallidos de acceso en ese periodo.");
        }
        List<LoginFallidoDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            LoginFallidoDTO dto = new LoginFallidoDTO();
            dto.setUsuario((String) fila[0]);
            dto.setIntentosFallidos(((Number) fila[1]).longValue());
            dto.setUltimoIntento(aFechaHora(fila[2]));
            dto.setIpsDistintas(((Number) fila[3]).longValue());
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }

    private static String vacioANull(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private static String mayusculas(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim().toUpperCase();
    }

    private static LocalDateTime aFechaHora(Object valor) {
        if (valor == null) return null;
        if (valor instanceof LocalDateTime f) return f;
        return ((Timestamp) valor).toLocalDateTime();
    }
}
