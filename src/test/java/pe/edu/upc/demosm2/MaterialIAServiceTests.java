package pe.edu.upc.demosm2;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upc.demosm2.serviceimplements.MaterialIAServiceImplement;
import static org.junit.jupiter.api.Assertions.*;

class MaterialIAServiceTests {
    @Test void modeloNoConfiguradoNoSeInvoca() {
        var service=new MaterialIAServiceImplement("http://127.0.0.1:1","");
        assertEquals(503,assertThrows(ResponseStatusException.class,()->service.consultar("uno",UUID.randomUUID().toString())).getStatusCode().value());
    }
    @Test void rechazaRutasFueraDeLosArchivosPermitidos() {
        var service=new MaterialIAServiceImplement("http://127.0.0.1:1","clave-de-prueba-de-al-menos-32-caracteres");
        assertThrows(IllegalArgumentException.class,()->service.archivo("uno",UUID.randomUUID().toString(),"../config.json"));
        assertThrows(IllegalArgumentException.class,()->service.consultar("uno","../../otro"));
    }
    @Test void losTrabajosSeAislanPorUsuario() throws Exception {
        List<String> paths=new ArrayList<>();
        var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        server.createContext("/",exchange->{paths.add(exchange.getRequestURI().getPath());exchange.sendResponseHeaders(404,-1);exchange.close();});
        server.start();
        try {
            var service=new MaterialIAServiceImplement("http://127.0.0.1:"+server.getAddress().getPort(),"clave-de-prueba-de-al-menos-32-caracteres");
            String id=UUID.randomUUID().toString();
            assertThrows(ResponseStatusException.class,()->service.consultar("uno",id));
            assertThrows(ResponseStatusException.class,()->service.consultar("dos",id));
            assertNotEquals(paths.get(0),paths.get(1));
            assertTrue(paths.stream().allMatch(path->path.matches("/v1/materiales/[a-f0-9]{64}")));
        } finally {server.stop(0);}
    }
}
