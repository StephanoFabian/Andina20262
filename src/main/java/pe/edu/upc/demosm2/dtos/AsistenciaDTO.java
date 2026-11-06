package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;
import java.time.*;

public class AsistenciaDTO {

    private Long idAsistencia;
    @NotBlank @Pattern(regexp="PRESENTE|AUSENTE|TARDANZA|JUSTIFICADA")
    private String estado;
    @Size(max=500)
    private String observacion;
    @NotNull @Positive
    private Long idSesionVirtual;
    @NotNull @Positive
    private Long idPersona;
    public Long getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(Long value) { this.idAsistencia = value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { this.estado = value; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String value) { this.observacion = value; }
    public Long getIdSesionVirtual() { return idSesionVirtual; }
    public void setIdSesionVirtual(Long value) { this.idSesionVirtual = value; }
    public Long getIdPersona() { return idPersona; }
    public void setIdPersona(Long value) { this.idPersona = value; }
}
