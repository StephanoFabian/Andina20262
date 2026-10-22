package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;

// HU46: actualización parcial; solo se cambian los campos que llegan con valor.
public class PerfilAcademicoParcialDTO {
    @DecimalMin(value = "0.0", message = "notasPA va de 0 a 20")
    @DecimalMax(value = "20.0", message = "notasPA va de 0 a 20")
    private Double notasPA;

    @Size(max = 100, message = "estadoPsicologico admite como máximo 100 caracteres")
    private String estadoPsicologico;

    public PerfilAcademicoParcialDTO() {
    }

    public Double getNotasPA() {
        return notasPA;
    }

    public void setNotasPA(Double notasPA) {
        this.notasPA = notasPA;
    }

    public String getEstadoPsicologico() {
        return estadoPsicologico;
    }

    public void setEstadoPsicologico(String estadoPsicologico) {
        this.estadoPsicologico = estadoPsicologico;
    }
}
