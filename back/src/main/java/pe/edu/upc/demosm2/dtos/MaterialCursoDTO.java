package pe.edu.upc.demosm2.dtos;

public class MaterialCursoDTO {
    private Long idMaterial;
    private Long idCurso;

    public Long getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Long idMaterial) {
        this.idMaterial = idMaterial;
    }

    public Long getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Long idCurso) {
        this.idCurso = idCurso;
    }
    private String material;
    public String getMaterial() {return material;}
    public void setMaterial(String value) {this.material=value;}
    private String curso;
    public String getCurso() {return curso;}
    public void setCurso(String value) {this.curso=value;}
}
