package pe.edu.upc.demosm2.dtos;

public class RetiroPorCursoDTO {
    private String curso;

    private String area;

    private Long totalMatriculados;

    private Long retirados;

    private Long trasladados;

    private Double porcentajePerdida;

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Long getTotalMatriculados() {
        return totalMatriculados;
    }

    public void setTotalMatriculados(Long totalMatriculados) {
        this.totalMatriculados = totalMatriculados;
    }

    public Long getRetirados() {
        return retirados;
    }

    public void setRetirados(Long retirados) {
        this.retirados = retirados;
    }

    public Long getTrasladados() {
        return trasladados;
    }

    public void setTrasladados(Long trasladados) {
        this.trasladados = trasladados;
    }

    public Double getPorcentajePerdida() {
        return porcentajePerdida;
    }

    public void setPorcentajePerdida(Double porcentajePerdida) {
        this.porcentajePerdida = porcentajePerdida;
    }
}
