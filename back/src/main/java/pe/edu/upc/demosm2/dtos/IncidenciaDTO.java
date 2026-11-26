package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;
import java.time.*;

public class IncidenciaDTO {

    private Long idIncidencia;
    @NotBlank @Size(max=150)
    private String titulo;
    @NotBlank @Size(max=2000)
    private String descripcion;
    @NotNull
    private LocalDate fecha;
    @NotBlank @Pattern(regexp="ABIERTA|EN_PROCESO|RESUELTA|CERRADA")
    private String estado;
    @NotBlank @Pattern(regexp="BAJA|MEDIA|ALTA|CRITICA")
    private String prioridad;
    @NotNull @Positive
    private Long idPersona;
    @NotNull @Positive
    private Long idColegio;
    public Long getIdIncidencia() { return idIncidencia; }
    public void setIdIncidencia(Long value) { this.idIncidencia = value; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String value) { this.titulo = value; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String value) { this.descripcion = value; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate value) { this.fecha = value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { this.estado = value; }
    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String value) { this.prioridad = value; }
    public Long getIdPersona() { return idPersona; }
    public void setIdPersona(Long value) { this.idPersona = value; }
    public Long getIdColegio() { return idColegio; }
    public void setIdColegio(Long value) { this.idColegio = value; }
}
