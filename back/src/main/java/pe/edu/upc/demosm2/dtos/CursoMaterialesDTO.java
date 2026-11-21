package pe.edu.upc.demosm2.dtos;

public class CursoMaterialesDTO {
    private String curso;
    private Long cantidadMateriales;

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public Long getCantidadMateriales() {
        return cantidadMateriales;
    }

    public void setCantidadMateriales(Long cantidadMateriales) {
        this.cantidadMateriales = cantidadMateriales;
    }
}
