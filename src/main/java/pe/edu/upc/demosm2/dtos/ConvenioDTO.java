package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;
import java.time.*;

public class ConvenioDTO {

    private Long idConvenio;
    @NotBlank @Size(max=200)
    private String nombre;
    @Size(max=200)
    private String institucion;
    @NotNull
    private LocalDate fechaInicio;
    @NotNull
    private LocalDate fechaFin;
    @Pattern(regexp="VIGENTE|VENCIDO|CANCELADO|EN_RENOVACION|FINALIZADO")
    private String estado;
    @Positive
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
    @Size(max=200)
    private String entidad;
    @Pattern(regexp="GOBIERNO_REGIONAL|COOPERACION_INTERNACIONAL|OTRO")
    private String tipoEntidad;
    @Size(max=50)
    private String region;
    @DecimalMin("0") @Digits(integer=12,fraction=2)
    private java.math.BigDecimal presupuesto;
    public String getEntidad() { return entidad; }
    public void setEntidad(String value) { entidad=value; }
    public String getTipoEntidad() { return tipoEntidad; }
    public void setTipoEntidad(String value) { tipoEntidad=value; }
    public String getRegion() { return region; }
    public void setRegion(String value) { region=value; }
    public java.math.BigDecimal getPresupuesto() { return presupuesto; }
    public void setPresupuesto(java.math.BigDecimal value) { presupuesto=value; }
    @AssertTrue(message="La entidad o institución es obligatoria")
    public boolean isEntidadValida() { return (entidad!=null && !entidad.isBlank()) || (institucion!=null && !institucion.isBlank()); }
}
