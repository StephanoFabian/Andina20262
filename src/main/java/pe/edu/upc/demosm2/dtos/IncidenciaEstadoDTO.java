package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;

public class IncidenciaEstadoDTO {
    @NotBlank(message = "estado es obligatorio")
    @Pattern(regexp = "(?i)ABIERTA|EN_PROCESO|RESUELTA|CERRADA", message = "estado debe ser ABIERTA, EN_PROCESO, RESUELTA o CERRADA")
    private String estado;

    public IncidenciaEstadoDTO() {
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
