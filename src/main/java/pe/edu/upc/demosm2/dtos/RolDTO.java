package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;

public class RolDTO {
    private Long idTipoPersona;

    @NotBlank(message = "detalle es obligatorio")
    @Size(max = 50, message = "detalle admite como máximo 50 caracteres")
    private String detalle;

    public RolDTO() {
    }

    public Long getIdTipoPersona() {
        return idTipoPersona;
    }

    public void setIdTipoPersona(Long idTipoPersona) {
        this.idTipoPersona = idTipoPersona;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
