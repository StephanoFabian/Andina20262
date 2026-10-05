package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class CapacitacionDocenteDTO {
    private Long idCapacitacion;

    @NotNull(message = "idPersona es obligatorio")
    private Long idPersona;

    @NotBlank(message = "modulo es obligatorio")
    @Size(max = 100, message = "modulo admite como máximo 100 caracteres")
    private String modulo;

    // Vacío mientras el taller sigue en curso
    @DecimalMin(value = "0.0", message = "puntaje va de 0 a 20")
    @DecimalMax(value = "20.0", message = "puntaje va de 0 a 20")
    private Double puntaje;

    @PastOrPresent(message = "fechaEvaluacion no puede ser futura")
    private LocalDate fechaEvaluacion;

    // Salida: EN_CURSO, APROBADO (puntaje >= 11) o DESAPROBADO
    private String estado;

    public CapacitacionDocenteDTO() {
    }

    public Long getIdCapacitacion() {
        return idCapacitacion;
    }

    public void setIdCapacitacion(Long idCapacitacion) {
        this.idCapacitacion = idCapacitacion;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public Double getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(Double puntaje) {
        this.puntaje = puntaje;
    }

    public LocalDate getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDate fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
