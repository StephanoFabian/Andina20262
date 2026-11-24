package pe.edu.upc.demosm2;

import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upc.demosm2.dtos.*;
import pe.edu.upc.demosm2.serviceimplements.VideollamadaServiceImplement;
import pe.edu.upc.demosm2.serviceinterfaces.ISesionVirtualService;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VideollamadaServiceTests {
    private static final String SECRET = "clave-de-prueba-videollamada-32-caracteres-123";
    private ISesionVirtualService sesiones() {
        ISesionVirtualService mock = mock(ISesionVirtualService.class);
        SesionVirtualDTO dto = new SesionVirtualDTO(); dto.setTitulo("Sesión de prueba");
        when(mock.buscar(anyLong())).thenReturn(dto);
        return mock;
    }
    private VideollamadaServiceImplement servicio(String iaUrl) {
        return new VideollamadaServiceImplement(sesiones(), "ws://localhost:7880", "testkey", SECRET, iaUrl, SECRET);
    }

    @Test void tokenFirmadoLimitadoASalaYConCaducidad() throws Exception {
        var service = servicio("http://127.0.0.1:1");
        var acceso = service.acceso(1L, "persona-privada");
        SignedJWT jwt = SignedJWT.parse(acceso.getToken());
        assertTrue(jwt.verify(new MACVerifier(SECRET)));
        var claims = jwt.getJWTClaimsSet();
        assertEquals("testkey", claims.getIssuer());
        assertEquals(acceso.getSala(), ((Map<?, ?>) claims.getClaim("video")).get("room"));
        assertEquals(false, ((Map<?, ?>) claims.getClaim("video")).get("roomAdmin"));
        assertTrue(claims.getExpirationTime().toInstant().isBefore(Instant.now().plusSeconds(301)));
        assertFalse(claims.toString().contains("persona-privada"));
        assertNotEquals(acceso.getSala(), service.acceso(2L, "persona-privada").getSala());
        assertNotEquals(acceso.getIdentidad(), service.acceso(1L, "otro-usuario").getIdentidad());
    }

    @Test void configuracionAusenteNoGeneraTokenInseguro() {
        var service = new VideollamadaServiceImplement(sesiones(), "ws://localhost:7880", "", "", "http://127.0.0.1:1", "");
        assertEquals(503, assertThrows(ResponseStatusException.class, () -> service.acceso(1L, "persona")).getStatusCode().value());
    }

    @Test void rechazaAudioGrandeIdiomaInvalidoYUuidInvalido() {
        var service = servicio("http://127.0.0.1:1");
        var dto = new FragmentoTraduccionDTO();
        dto.setId(UUID.randomUUID().toString()); dto.setIdiomaOrigen("en"); dto.setAudioBase64("YQ==");
        assertThrows(IllegalArgumentException.class, () -> service.enviar(1L, "persona", dto));
        dto.setIdiomaOrigen("es"); dto.setAudioBase64("A".repeat(1_400_001));
        assertThrows(IllegalArgumentException.class, () -> service.enviar(1L, "persona", dto));
        assertThrows(IllegalArgumentException.class, () -> service.resultado(1L, "persona", "../otro"));
    }

    @Test void mismoUuidQuedaAisladoEntreUsuariosYSesiones() throws Exception {
        List<String> paths = new ArrayList<>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            paths.add(exchange.getRequestURI().getPath());
            assertEquals(SECRET, exchange.getRequestHeaders().getFirst("X-Andina-Key"));
            byte[] body = "{}".getBytes(); exchange.sendResponseHeaders(404, body.length);
            exchange.getResponseBody().write(body); exchange.close();
        });
        server.start();
        try {
            var service = servicio("http://127.0.0.1:" + server.getAddress().getPort());
            String id = UUID.randomUUID().toString();
            assertThrows(ResponseStatusException.class, () -> service.resultado(1L, "uno", id));
            assertThrows(ResponseStatusException.class, () -> service.resultado(1L, "dos", id));
            assertThrows(ResponseStatusException.class, () -> service.resultado(2L, "uno", id));
            assertEquals(3, new HashSet<>(paths).size());
            assertTrue(paths.stream().allMatch(path -> path.matches("/v1/fragmentos/[a-f0-9]{64}")));
        } finally { server.stop(0); }
    }
}
