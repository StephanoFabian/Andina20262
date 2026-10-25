package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class AsistenciaDTO {
    private Long idAsistencia;

    @NotNull(message = "idPersona es obligatorio")
    private Long idPersona;

    @NotNull(message = "idCurso es obligatorio")
    private Long idCurso;

    @NotNull(message = "idPeriodo es obligatorio")
    private Long idPeriodo;

    @NotNull(message = "fechaSesion es obligatoria")
    @PastOrPresent(message = "fechaSesion no puede ser futura")
    private LocalDate fechaSesion;

    @NotBlank(message = "estado es obligatorio")
    @Pattern(regexp = "(?i)PRESENTE|TARDANZA|AUSENTE|JUSTIFICADO", message = "estado debe ser PRESENTE, TARDANZA, AUSENTE o JUSTIFICADO")
    private String estado;

    @DecimalMin(value = "0.0", message = "calificacion va de 0 a 20")
    @DecimalMax(value = "20.0", message = "calificacion va de 0 a 20")
    private Double calificacion;

    @Size(max = 200, message = "observacion admite como máximo 200 caracteres")
    private String observacion;

    public AsistenciaDTO() {
    }

    public Long getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(Long idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public Long getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Long idCurso) {
        this.idCurso = idCurso;
    }

    public Long getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(Long idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public LocalDate getFechaSesion() {
        return fechaSesion;
    }

    public void setFechaSesion(LocalDate fechaSesion) {
        this.fechaSesion = fechaSesion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
