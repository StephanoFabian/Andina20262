package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;
import java.time.*;

public class SesionVirtualDTO {

    private Long idSesionVirtual;
    @NotBlank @Size(max=150)
    private String titulo;
    @NotNull
    private LocalDate fecha;
    @NotNull
    private LocalTime horaInicio;
    @NotNull
    private LocalTime horaFin;
    @NotBlank @Size(max=500) @Pattern(regexp="https://[^\\s]+", message="Debe ser una URL HTTPS")
    private String enlace;
    @NotBlank @Pattern(regexp="PROGRAMADA|EN_CURSO|FINALIZADA|CANCELADA")
    private String estado;
    @NotNull @Positive
    private Long idAsignacion;
    public Long getIdSesionVirtual() { return idSesionVirtual; }
    public void setIdSesionVirtual(Long value) { this.idSesionVirtual = value; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String value) { this.titulo = value; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate value) { this.fecha = value; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime value) { this.horaInicio = value; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime value) { this.horaFin = value; }
    public String getEnlace() { return enlace; }
    public void setEnlace(String value) { this.enlace = value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { this.estado = value; }
    public Long getIdAsignacion() { return idAsignacion; }
    public void setIdAsignacion(Long value) { this.idAsignacion = value; }
}
