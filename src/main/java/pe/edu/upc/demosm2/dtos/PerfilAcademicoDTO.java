package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;

public class PerfilAcademicoDTO {
    private Long idPerfilAcademico;

    // Estudiante dueño del perfil (uno por estudiante)
    @NotNull(message = "idPersona es obligatorio")
    private Long idPersona;

    @NotBlank(message = "detallePA es obligatorio")
    @Size(max = 100, message = "detallePA admite como máximo 100 caracteres")
    private String detallePA;

    @NotNull(message = "notasPA es obligatorio")
    @DecimalMin(value = "0.0", message = "notasPA va de 0 a 20")
    @DecimalMax(value = "20.0", message = "notasPA va de 0 a 20")
    private Double notasPA;

    // Confidencial: solo lo consultan ADMIN, ADMIN_ESCUELA, ESPECIALISTA y LOCAL
    @Size(max = 100, message = "estadoPsicologico admite como máximo 100 caracteres")
    private String estadoPsicologico;

    public PerfilAcademicoDTO() {
    }

    public Long getIdPerfilAcademico() {
        return idPerfilAcademico;
    }

    public void setIdPerfilAcademico(Long idPerfilAcademico) {
        this.idPerfilAcademico = idPerfilAcademico;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public String getDetallePA() {
        return detallePA;
    }

    public void setDetallePA(String detallePA) {
        this.detallePA = detallePA;
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
