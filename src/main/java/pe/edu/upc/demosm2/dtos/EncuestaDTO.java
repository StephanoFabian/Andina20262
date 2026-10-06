package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class EncuestaDTO {
    private Long idEncuesta;

    @NotBlank(message = "titulo es obligatorio")
    @Size(max = 150, message = "titulo admite como máximo 150 caracteres")
    private String titulo;

    @Size(max = 500, message = "descripcion admite como máximo 500 caracteres")
    private String descripcion;

    @NotNull(message = "fechaInicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "fechaFin es obligatoria")
    private LocalDate fechaFin;

    // Si no se envía, queda ABIERTA
    @Pattern(regexp = "(?i)ABIERTA|CERRADA", message = "estado debe ser ABIERTA o CERRADA")
    private String estado;

    public EncuestaDTO() {
    }

    public Long getIdEncuesta() {
        return idEncuesta;
    }

    public void setIdEncuesta(Long idEncuesta) {
        this.idEncuesta = idEncuesta;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
