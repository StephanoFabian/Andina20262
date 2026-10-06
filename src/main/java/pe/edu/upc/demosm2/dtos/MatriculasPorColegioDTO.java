package pe.edu.upc.demosm2.dtos;

public class MatriculasPorColegioDTO {
    private String colegio;

    private String departamento;

    private String tipoZona;

    private Long totalMatriculas;

    private Double porcentajeDelTotal;

    public String getColegio() {
        return colegio;
    }

    public void setColegio(String colegio) {
        this.colegio = colegio;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getTipoZona() {
        return tipoZona;
    }

    public void setTipoZona(String tipoZona) {
        this.tipoZona = tipoZona;
    }

    public Long getTotalMatriculas() {
        return totalMatriculas;
    }

    public void setTotalMatriculas(Long totalMatriculas) {
        this.totalMatriculas = totalMatriculas;
    }

    public Double getPorcentajeDelTotal() {
        return porcentajeDelTotal;
    }

    public void setPorcentajeDelTotal(Double porcentajeDelTotal) {
        this.porcentajeDelTotal = porcentajeDelTotal;
    }
}
