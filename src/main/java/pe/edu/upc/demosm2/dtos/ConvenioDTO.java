package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;
import java.time.*;

public class ConvenioDTO {

    private Long idConvenio;
    @NotBlank @Size(max=150)
    private String nombre;
    @NotBlank @Size(max=150)
    private String institucion;
    @NotNull
    private LocalDate fechaInicio;
    @NotNull
    private LocalDate fechaFin;
    @NotBlank @Pattern(regexp="VIGENTE|VENCIDO|CANCELADO")
    private String estado;
    @NotNull @Positive
    private Long idColegio;
    public Long getIdConvenio() { return idConvenio; }
    public void setIdConvenio(Long value) { this.idConvenio = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public String getInstitucion() { return institucion; }
    public void setInstitucion(String value) { this.institucion = value; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate value) { this.fechaInicio = value; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate value) { this.fechaFin = value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { this.estado = value; }
    public Long getIdColegio() { return idColegio; }
    public void setIdColegio(Long value) { this.idColegio = value; }
}
