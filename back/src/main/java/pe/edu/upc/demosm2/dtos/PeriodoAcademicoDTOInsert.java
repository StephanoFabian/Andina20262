package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class PeriodoAcademicoDTOInsert {
    private Long idPeriodo;

    @NotBlank(message = "nombre es obligatorio")
    @Size(max = 50, message = "nombre admite como máximo 50 caracteres")
    private String nombre;

    @NotNull(message = "fechaInicio es obligatorio")
    private LocalDate fechaInicio;

    @NotNull(message = "fechaFin es obligatorio")
    private LocalDate fechaFin;

    @Size(max = 20, message = "estado admite como máximo 20 caracteres")
    private String estado;

    public Long getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(Long idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
