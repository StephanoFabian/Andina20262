package pe.edu.upc.demosm2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import pe.edu.upc.demosm2.securities.JwtTokenService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=${ANDINA_TEST_DB_URL:jdbc:postgresql://127.0.0.1:55439/andina_test}",
        "spring.datasource.username=${ANDINA_TEST_DB_USERNAME:andina_test}",
        "spring.datasource.password=${ANDINA_TEST_DB_PASSWORD:}",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=integration-test-only-01234567890123456789012345678901234567890123456789"
})
class ColegiosAulasIntegrationTests {
    @LocalServerPort int port;
    @Autowired JwtTokenService jwt;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate db;
    final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    String admin;
    String escuela;
    String docente;

    @BeforeEach
    void prepare() {
        db.update("DELETE FROM aulas");
        db.update("DELETE FROM colegios");
        admin = token("ADMIN");
        escuela = token("ADMIN_ESCUELA");
        docente = token("DOCENTE");
    }

    private String token(String role) {
        return jwt.generateToken(User.withUsername("1000").password("unused").roles(role).build());
    }

    private HttpResponse<String> request(String method, String path, String body, String token) throws Exception {
        HttpRequest.Builder req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
                .timeout(Duration.ofSeconds(15)).header("Content-Type", "application/json");
        if (token != null) req.header("Authorization", "Bearer " + token);
        req.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        return client.send(req.build(), HttpResponse.BodyHandlers.ofString());
    }

    private JsonNode body(HttpResponse<String> response) { return json.readTree(response.body()); }

    private String colegio(String name) {
        return "{\"nombre\":\"" + name + "\",\"departamento\":\"Cusco\",\"provincia\":\"Cusco\","
                + "\"distrito\":\"San Sebastian\",\"comunidad\":\"Demo\",\"tipo_zona\":\"RURAL\"}";
    }

    private long createColegio(String name) throws Exception {
        var r = request("POST", "/api/colegios", colegio(name), admin);
        assertEquals(201, r.statusCode(), r.body());
        long id = body(r).path("idColegio").asLong();
        assertTrue(id > 0);
        assertTrue(r.headers().firstValue("Location").orElseThrow().endsWith("/" + id));
        return id;
    }

    private String aula(long colegio, int capacidad) {
        return "{\"numero\":\"Salon 101\",\"seccion\":\"A\",\"capacidad\":" + capacidad + ",\"idColegio\":" + colegio + "}";
    }

    @Test
    void colegioCrudPreservesItsIdAndReturns404AfterDeletion() throws Exception {
        long id = createColegio("Colegio inicial");
        var changed = request("PUT", "/api/colegios/" + id, colegio("Colegio actualizado"), escuela);
        assertEquals(200, changed.statusCode(), changed.body());
        assertEquals(id, body(changed).path("idColegio").asLong());
        assertEquals("Colegio actualizado", body(request("GET", "/api/colegios/" + id, null, admin)).path("nombre").asText());
        assertEquals(204, request("DELETE", "/api/colegios/" + id, null, admin).statusCode());
        assertEquals(404, request("GET", "/api/colegios/" + id, null, admin).statusCode());
        assertEquals(404, request("PUT", "/api/colegios/" + id, colegio("Inexistente"), admin).statusCode());
    }

    @Test
    void aulaCrudSavesAndChangesTheRealColegioRelationship() throws Exception {
        long first = createColegio("Primero"), second = createColegio("Segundo");
        var created = request("POST", "/api/aula", aula(first, 25), escuela);
        assertEquals(201, created.statusCode(), created.body());
        long id = body(created).path("idAula").asLong();
        assertEquals(first, body(created).path("idColegio").asLong());
        assertEquals("Salon 101", body(created).path("nombre").asText());
        assertEquals(first, db.queryForObject("SELECT id_colegio FROM aulas WHERE id_aula=?", Long.class, id));
        var changed = request("PUT", "/api/aula/" + id, aula(second, 40), admin);
        assertEquals(200, changed.statusCode(), changed.body());
        assertEquals(second, body(changed).path("idColegio").asLong());
        assertEquals(40, body(request("GET", "/api/aula/" + id, null, admin)).path("capacidad").asInt());
        assertEquals(204, request("DELETE", "/api/aula/" + id, null, escuela).statusCode());
        assertEquals(404, request("GET", "/api/aula/" + id, null, admin).statusCode());
    }

    @Test
    void validationRejectsInvalidNumbersMissingParentsAndOversizeFields() throws Exception {
        long id = createColegio("Validacion");
        for (int capacity : new int[]{0, -1})
            assertEquals(400, request("POST", "/api/aula", aula(id, capacity), admin).statusCode());
        assertEquals(400, request("POST", "/api/aula", "{\"nombre\":\"A\",\"seccion\":\"A\",\"capacidad\":20}", admin).statusCode());
        assertEquals(404, request("POST", "/api/aula", aula(9999999, 20), admin).statusCode());
        assertEquals(400, request("POST", "/api/colegios", colegio(" "), admin).statusCode());
        assertEquals(400, request("POST", "/api/colegios", colegio("a".repeat(31)), admin).statusCode());
        assertEquals(400, request("POST", "/api/aula", "{broken", admin).statusCode());
        assertEquals(0, db.queryForObject("SELECT count(*) FROM aulas", Integer.class));
    }

    @Test
    void dependentColegioDeletionReturnsConflictAndKeepsBothRecords() throws Exception {
        long id = createColegio("Con aula");
        assertEquals(201, request("POST", "/api/aula", aula(id, 30), admin).statusCode());
        var deleted = request("DELETE", "/api/colegios/" + id, null, admin);
        assertEquals(409, deleted.statusCode(), deleted.body());
        assertFalse(deleted.body().contains("fkgu"));
        assertEquals(1, db.queryForObject("SELECT count(*) FROM colegios", Integer.class));
        assertEquals(1, db.queryForObject("SELECT count(*) FROM aulas", Integer.class));
    }

    @Test
    void onlyAdministratorsCanModifyColegiosAndAulas() throws Exception {
        long colegio = createColegio("Permisos");
        long idAula = body(request("POST", "/api/aula", aula(colegio, 20), admin)).path("idAula").asLong();
        assertEquals(401, request("GET", "/api/colegios", null, null).statusCode());
        for (String path : new String[]{"/api/colegios/" + colegio, "/api/aula/" + idAula}) {
            assertEquals(403, request("DELETE", path, null, docente).statusCode());
        }
        assertEquals(403, request("POST", "/api/colegios", colegio("No autorizado"), docente).statusCode());
        assertEquals(403, request("PUT", "/api/aula/" + idAula, aula(colegio, 10), docente).statusCode());
        assertEquals(200, request("GET", "/api/aula", null, docente).statusCode());
    }

    @Test
    void suppliedIdsCannotTurnCreateIntoUpdateOrRedirectPut() throws Exception {
        long id = createColegio("Original");
        String withId = colegio("Alterado").replace("{", "{\"idColegio\":" + id + ",");
        assertEquals(400, request("POST", "/api/colegios", withId, admin).statusCode());
        assertEquals(400, request("PUT", "/api/colegios/" + (id + 1), withId, admin).statusCode());
        assertEquals("Original", db.queryForObject("SELECT nombre FROM colegios WHERE id_colegio=?", String.class, id));
    }

    @Test
    void paginationIsBoundedOrderedAndFiltersAtTheDatabase() throws Exception {
        long first = createColegio("Carga A"), second = createColegio("Carga B");
        db.update("INSERT INTO aulas(nombre,seccion,capacidad,id_colegio) SELECT 'Aula '||n,'A',30,? FROM generate_series(1,205) n", first);
        db.update("INSERT INTO aulas(nombre,seccion,capacidad,id_colegio) SELECT 'Otra '||n,'B',30,? FROM generate_series(1,10) n", second);
        var page0 = request("GET", "/api/aula?idColegio=" + first + "&size=100", null, admin);
        var page1 = request("GET", "/api/aula?idColegio=" + first + "&size=100&page=1", null, admin);
        var page2 = request("GET", "/api/aula?idColegio=" + first + "&size=100&page=2", null, admin);
        assertEquals(200, page0.statusCode(), page0.body());
        assertEquals(100, body(page0).size());
        assertEquals(100, body(page1).size());
        assertEquals(5, body(page2).size());
        assertEquals("true", page0.headers().firstValue("X-Has-Next").orElseThrow());
        assertEquals("false", page2.headers().firstValue("X-Has-Next").orElseThrow());
        assertTrue(body(page1).get(0).path("idAula").asLong() > body(page0).get(99).path("idAula").asLong());
        for (JsonNode row : body(page0)) assertEquals(first, row.path("idColegio").asLong());
        assertEquals(20, body(request("GET", "/api/aula", null, admin)).size());
        assertEquals(10, body(request("GET", "/api/aula?idColegio=" + second, null, admin)).size());
        assertEquals(400, request("GET", "/api/aula?size=101", null, admin).statusCode());
        assertEquals(400, request("GET", "/api/colegios?page=-1", null, admin).statusCode());
        assertEquals(400, request("GET", "/api/aula?idColegio=abc", null, admin).statusCode());
        assertEquals(404, request("GET", "/api/aula?idColegio=9999999", null, admin).statusCode());
    }
}
