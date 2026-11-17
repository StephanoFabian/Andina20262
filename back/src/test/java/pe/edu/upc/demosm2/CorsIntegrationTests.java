package pe.edu.upc.demosm2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CorsIntegrationTests {
    @Value("${local.server.port}")
    private int port;

    private final HttpClient client = HttpClient.newHttpClient();

    private HttpResponse<String> preflight(String origin, String method) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/convenios/1"))
                .header("Origin", origin)
                .header("Access-Control-Request-Method", method)
                .header("Access-Control-Request-Headers", "authorization,content-type")
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void aceptaAmbosOrigenesLocalesYLosMetodosDelCrud() throws Exception {
        for (String origin : new String[]{"http://localhost:4200", "http://127.0.0.1:4200"}) {
            for (String method : new String[]{"GET", "POST", "PUT", "DELETE"}) {
                HttpResponse<String> response = preflight(origin, method);
                assertEquals(200, response.statusCode());
                assertEquals(origin, response.headers().firstValue("Access-Control-Allow-Origin").orElseThrow());
                assertTrue(response.headers().firstValue("Access-Control-Allow-Headers").orElseThrow()
                        .toLowerCase().contains("authorization"));
                assertTrue(response.headers().firstValue("Access-Control-Allow-Credentials").isEmpty());
            }
        }
    }

    @Test
    void rechazaOrigenAjeno() throws Exception {
        HttpResponse<String> response = preflight("http://example.test", "GET");
        assertEquals(403, response.statusCode());
        assertTrue(response.headers().firstValue("Access-Control-Allow-Origin").isEmpty());
    }

    @Test
    void rechazaMetodoNoPermitido() throws Exception {
        assertEquals(403, preflight("http://localhost:4200", "TRACE").statusCode());
    }

    @Test
    void sigueExigiendoJwtYExponeElErrorAlFrontendPermitido() throws Exception {
        HttpResponse<String> response = client.send(HttpRequest.newBuilder(
                        URI.create("http://127.0.0.1:" + port + "/api/grados/pagina"))
                .header("Origin", "http://localhost:4200").GET().build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(401, response.statusCode());
        assertEquals("http://localhost:4200", response.headers().firstValue("Access-Control-Allow-Origin").orElseThrow());
    }

    @Test
    void aplicaCorsGlobalAlLoginYConservaSuValidacion() throws Exception {
        HttpResponse<String> response = client.send(HttpRequest.newBuilder(
                        URI.create("http://127.0.0.1:" + port + "/login"))
                .header("Origin", "http://localhost:4200")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertEquals("http://localhost:4200", response.headers().firstValue("Access-Control-Allow-Origin").orElseThrow());
    }
}
