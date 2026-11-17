package pe.edu.upc.demosm2.serviceimplements;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upc.demosm2.dtos.MaterialIADTO;
import pe.edu.upc.demosm2.serviceinterfaces.IMaterialIAService;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Service
public class MaterialIAServiceImplement implements IMaterialIAService {
    private final String clave;
    private final RestClient api;
    public MaterialIAServiceImplement(@Value("${andina.material-ia.url:http://127.0.0.1:8096}") String url,
                                      @Value("${andina.material-ia.api-key:}") String clave) {
        this.clave = clave;
        var factory = new JdkClientHttpRequestFactory(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build());
        factory.setReadTimeout(Duration.ofSeconds(8));
        this.api = RestClient.builder().baseUrl(url).requestFactory(new BufferingClientHttpRequestFactory(factory))
                .defaultHeader("X-Andina-Key", clave).build();
    }
    private String firmar(String value) {
        if (clave.length() < 32) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El generador de materiales no está configurado");
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(clave.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) { throw new IllegalStateException("No se pudo identificar el trabajo", ex); }
    }
    private String trabajo(String usuario, String id) {
        if (id == null || !id.matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"))
            throw new IllegalArgumentException("El material necesita un UUID válido");
        return firmar("material:" + usuario + ":" + id);
    }
    public MaterialIADTO generar(String usuario, MaterialIADTO entrada) {
        MaterialIADTO dto = new MaterialIADTO();
        dto.setId(trabajo(usuario, entrada.getId())); dto.setPropietario(firmar("usuario:" + usuario));
        dto.setTema(entrada.getTema()); dto.setNivel(entrada.getNivel());
        dto.setInstrucciones(Objects.toString(entrada.getInstrucciones(), ""));
        try {
            var result = api.post().uri("/v1/materiales").body(dto).retrieve().body(MaterialIADTO.class);
            return respuesta(result, entrada.getId());
        } catch (RestClientException ex) { throw error(ex); }
    }
    public MaterialIADTO consultar(String usuario, String id) {
        String job = trabajo(usuario, id);
        try {
            return respuesta(api.get().uri("/v1/materiales/{job}", job).retrieve().body(MaterialIADTO.class), id);
        } catch (RestClientException ex) { throw error(ex); }
    }
    public byte[] archivo(String usuario, String id, String nombre) {
        if (nombre == null || !nombre.matches("material\\.(pdf|docx|txt)|material-revisado\\.txt|figura-[1-3]\\.(png|svg)"))
            throw new IllegalArgumentException("Archivo no permitido");
        String job = trabajo(usuario, id);
        try {
            byte[] data = api.get().uri("/v1/materiales/{job}/archivos/{nombre}", job, nombre).retrieve().body(byte[].class);
            if (data == null || data.length > 8_000_000) throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Archivo fuera de los límites");
            return data;
        } catch (RestClientException ex) { throw error(ex); }
    }
    private MaterialIADTO respuesta(MaterialIADTO dto, String id) {
        if (dto == null) throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "El generador devolvió una respuesta vacía");
        dto.setId(id); dto.setPropietario(null); return dto;
    }
    private ResponseStatusException error(RestClientException ex) {
        if (ex instanceof RestClientResponseException response) {
            int code = response.getStatusCode().value();
            if (code == 404) return new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe ese material o archivo para tu usuario");
            if (code == 429) return new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "El generador está ocupado o ya tienes un material pendiente");
            if (code == 409) return new ResponseStatusException(HttpStatus.CONFLICT, "Ese identificador ya corresponde a otro material");
            if (code == 400) return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Revisa el tema, nivel e instrucciones");
        }
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo conectar con el generador local de materiales");
    }
}
