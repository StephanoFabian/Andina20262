package pe.edu.upc.demosm2.dtos;

public class PerfilAcademicoDTO {
    private Long idPerfilAcademico;
    private String detallePA;
    private Double notasPA;

    public Long getIdPerfilAcademico() {
        return idPerfilAcademico;
    }

    public void setIdPerfilAcademico(Long idPerfilAcademico) {
        this.idPerfilAcademico = idPerfilAcademico;
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
}
