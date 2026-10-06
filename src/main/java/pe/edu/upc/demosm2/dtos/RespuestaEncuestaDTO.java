package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class RespuestaEncuestaDTO {
    private Long idRespuesta;

    @NotNull(message = "idEncuesta es obligatorio")
    private Long idEncuesta;

    // Salida: quien responde (se toma del token)
    private Long idPersona;

    @NotNull(message = "satisfaccionGeneral es obligatorio")
    @Min(value = 1, message = "satisfaccionGeneral va de 1 a 5")
    @Max(value = 5, message = "satisfaccionGeneral va de 1 a 5")
    private Integer satisfaccionGeneral;

    @NotNull(message = "calidadContenido es obligatorio")
    @Min(value = 1, message = "calidadContenido va de 1 a 5")
    @Max(value = 5, message = "calidadContenido va de 1 a 5")
    private Integer calidadContenido;

    @NotNull(message = "facilidadUso es obligatorio")
    @Min(value = 1, message = "facilidadUso va de 1 a 5")
    @Max(value = 5, message = "facilidadUso va de 1 a 5")
    private Integer facilidadUso;

    @Size(max = 500, message = "comentario admite como máximo 500 caracteres")
    private String comentario;

    private LocalDateTime fechaRespuesta;

    public RespuestaEncuestaDTO() {
    }

    public Long getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(Long idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    public Long getIdEncuesta() {
        return idEncuesta;
    }

    public void setIdEncuesta(Long idEncuesta) {
        this.idEncuesta = idEncuesta;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public Integer getSatisfaccionGeneral() {
        return satisfaccionGeneral;
    }

    public void setSatisfaccionGeneral(Integer satisfaccionGeneral) {
        this.satisfaccionGeneral = satisfaccionGeneral;
    }

    public Integer getCalidadContenido() {
        return calidadContenido;
    }

    public void setCalidadContenido(Integer calidadContenido) {
        this.calidadContenido = calidadContenido;
    }

    public Integer getFacilidadUso() {
        return facilidadUso;
    }

    public void setFacilidadUso(Integer facilidadUso) {
        this.facilidadUso = facilidadUso;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }
}
