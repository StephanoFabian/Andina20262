package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ConvenioDTO {
    private Long idConvenio;

    @NotBlank(message = "nombre es obligatorio")
    @Size(max = 200, message = "nombre admite como máximo 200 caracteres")
    private String nombre;

    @NotBlank(message = "entidad es obligatorio")
    @Size(max = 200, message = "entidad admite como máximo 200 caracteres")
    private String entidad;

    @NotBlank(message = "tipoEntidad es obligatorio")
    @Pattern(regexp = "(?i)GOBIERNO_REGIONAL|COOPERACION_INTERNACIONAL|OTRO", message = "tipoEntidad debe ser GOBIERNO_REGIONAL, COOPERACION_INTERNACIONAL u OTRO")
    private String tipoEntidad;

    @NotBlank(message = "region es obligatorio")
    @Size(max = 50, message = "region admite como máximo 50 caracteres")
    private String region;

    @NotNull(message = "fechaInicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "fechaFin es obligatoria")
    private LocalDate fechaFin;

    @NotNull(message = "presupuesto es obligatorio")
    @PositiveOrZero(message = "presupuesto no puede ser negativo")
    @Digits(integer = 12, fraction = 2, message = "presupuesto admite 12 enteros y 2 decimales")
    private BigDecimal presupuesto;

    // Si no se envía, queda VIGENTE
    @Pattern(regexp = "(?i)VIGENTE|EN_RENOVACION|VENCIDO|FINALIZADO", message = "estado debe ser VIGENTE, EN_RENOVACION, VENCIDO o FINALIZADO")
    private String estado;

    public ConvenioDTO() {
    }

    public Long getIdConvenio() {
        return idConvenio;
    }

    public void setIdConvenio(Long idConvenio) {
        this.idConvenio = idConvenio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(String tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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

    public BigDecimal getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(BigDecimal presupuesto) {
        this.presupuesto = presupuesto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
