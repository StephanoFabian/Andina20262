package pe.edu.upc.demosm2.serviceimplements;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upc.demosm2.dtos.*;
import pe.edu.upc.demosm2.serviceinterfaces.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;

@Service
public class VideollamadaServiceImplement implements IVideollamadaService {
    private final ISesionVirtualService sesiones;
    private final String servidor, clave, secreto, iaClave;
    private final RestClient ia;

    public VideollamadaServiceImplement(ISesionVirtualService sesiones,
            @Value("${andina.video.url:ws://localhost:7880}") String servidor,
            @Value("${andina.video.api-key:}") String clave,
            @Value("${andina.video.api-secret:}") String secreto,
            @Value("${andina.ia.url:http://127.0.0.1:8095}") String iaUrl,
            @Value("${andina.ia.api-key:}") String iaClave) {
        this.sesiones = sesiones; this.servidor = servidor;
        this.clave = clave; this.secreto = secreto; this.iaClave = iaClave;
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build());
        factory.setReadTimeout(Duration.ofSeconds(8));
        this.ia = RestClient.builder().baseUrl(iaUrl).requestFactory(new org.springframework.http.client.BufferingClientHttpRequestFactory(factory))
                .defaultHeader("X-Andina-Key", iaClave).build();
    }

    private void comprobar(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Sesión inválida");
        sesiones.buscar(id);
        if (clave.isBlank() || secreto.getBytes(StandardCharsets.UTF_8).length < 32)
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Falta configurar el servidor de videollamadas");
    }

    /** Identificadores opacos: LiveKit no recibe DNI, correo ni credenciales de Andina. */
    private String opaco(String valor) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(valor.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) { throw new IllegalStateException("No se pudo firmar el acceso", ex); }
    }

    public AccesoVideollamadaDTO acceso(Long id, String usuario) {
        comprobar(id);
        String sala = "andina-" + opaco("sala:" + id).substring(0, 32);
        String identidad = "participante-" + opaco("usuario:" + usuario).substring(0, 24);
        Instant ahora = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder().issuer(clave).subject(identidad)
                .issueTime(Date.from(ahora)).notBeforeTime(Date.from(ahora.minusSeconds(5)))
                .expirationTime(Date.from(ahora.plusSeconds(300)))
                .claim("video", Map.of("room", sala, "roomJoin", true, "canPublish", true,
                        "canSubscribe", true, "canPublishData", true, "roomAdmin", false)).build();
        try {
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(new MACSigner(secreto.getBytes(StandardCharsets.UTF_8)));
            AccesoVideollamadaDTO dto = new AccesoVideollamadaDTO();
            dto.setUrl(servidor); dto.setToken(jwt.serialize()); dto.setSala(sala);
            dto.setIdentidad(identidad); dto.setTitulo(sesiones.buscar(id).getTitulo());
            dto.setTraduccionDisponible(!iaClave.isBlank());
            return dto;
        } catch (JOSEException ex) { throw new IllegalStateException("No se pudo firmar la sala", ex); }
    }

    private String trabajo(Long id, String usuario, String fragmento) {
        if (fragmento == null || !fragmento.matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"))
            throw new IllegalArgumentException("El fragmento necesita un UUID válido");
        return opaco("audio:" + id + ":" + usuario + ":" + fragmento);
    }

    public FragmentoTraduccionDTO enviar(Long id, String usuario, FragmentoTraduccionDTO entrada) {
        comprobar(id);
        if (iaClave.isBlank()) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "La traducción de voz no está configurada");
        String job = trabajo(id, usuario, entrada.getId());
        if (!Set.of("es", "quy").contains(Objects.toString(entrada.getIdiomaOrigen(), "")))
            throw new IllegalArgumentException("Idioma: es o quy (quechua de Ayacucho)");
        if (entrada.getAudioBase64() == null || entrada.getAudioBase64().isEmpty() || entrada.getAudioBase64().length() > 1_400_000)
            throw new IllegalArgumentException("El fragmento de audio debe ocupar como máximo 1 MB");
        FragmentoTraduccionDTO solicitud = new FragmentoTraduccionDTO();
        solicitud.setId(job); solicitud.setPropietario(opaco("usuario:" + usuario));
        solicitud.setIdiomaOrigen(entrada.getIdiomaOrigen());
        solicitud.setIdiomaDestino(entrada.getIdiomaOrigen().equals("es") ? "quy" : "es");
        solicitud.setAudioBase64(entrada.getAudioBase64());
        try {
            FragmentoTraduccionDTO resultado = ia.post().uri("/v1/fragmentos").body(solicitud).retrieve().body(FragmentoTraduccionDTO.class);
            if (resultado == null) throw new ResourceAccessException("Respuesta vacía");
            resultado.setId(entrada.getId()); resultado.setPropietario(null);
            return resultado;
        } catch (RestClientResponseException ex) { throw errorIa(ex); }
        catch (ResourceAccessException ex) { throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de voz no está disponible"); }
    }

    public FragmentoTraduccionDTO resultado(Long id, String usuario, String fragmento) {
        comprobar(id);
        if (iaClave.isBlank()) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "La traducción de voz no está configurada");
        String job = trabajo(id, usuario, fragmento);
        try {
            FragmentoTraduccionDTO resultado = ia.get().uri("/v1/fragmentos/{job}", job).retrieve().body(FragmentoTraduccionDTO.class);
            if (resultado == null) throw new ResourceAccessException("Respuesta vacía");
            resultado.setId(fragmento); resultado.setPropietario(null);
            return resultado;
        } catch (RestClientResponseException ex) { throw errorIa(ex); }
        catch (ResourceAccessException ex) { throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de voz no está disponible"); }
    }

    private ResponseStatusException errorIa(RestClientResponseException ex) {
        if (ex.getStatusCode().value() == 429)
            return new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "El servicio de traducción está ocupado; espera a terminar la frase anterior");
        if (ex.getStatusCode().value() == 404)
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe ese fragmento o ya caducó");
        if (ex.getStatusCode().value() == 409)
            return new ResponseStatusException(HttpStatus.CONFLICT, "Ese fragmento ya fue enviado con otro contenido");
        if (ex.getStatusCode().value() == 400)
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "El audio o sus idiomas no son válidos");
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de voz no está disponible");
    }
}
