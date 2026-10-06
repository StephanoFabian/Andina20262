package pe.edu.upc.demosm2.dtos;

public class PreparacionDigitalDTO {
    private String region;

    private String colegio;

    private Long docentesEvaluados;

    private Long evaluaciones;

    private Double promedioPuntaje;

    private Long aprobadas;

    private Double porcentajeAprobacion;

    public PreparacionDigitalDTO() {
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getColegio() {
        return colegio;
    }

    public void setColegio(String colegio) {
        this.colegio = colegio;
    }

    public Long getDocentesEvaluados() {
        return docentesEvaluados;
    }

    public void setDocentesEvaluados(Long docentesEvaluados) {
        this.docentesEvaluados = docentesEvaluados;
    }

    public Long getEvaluaciones() {
        return evaluaciones;
    }

    public void setEvaluaciones(Long evaluaciones) {
        this.evaluaciones = evaluaciones;
    }

    public Double getPromedioPuntaje() {
        return promedioPuntaje;
    }

    public void setPromedioPuntaje(Double promedioPuntaje) {
        this.promedioPuntaje = promedioPuntaje;
    }

    public Long getAprobadas() {
        return aprobadas;
    }

    public void setAprobadas(Long aprobadas) {
        this.aprobadas = aprobadas;
    }

    public Double getPorcentajeAprobacion() {
        return porcentajeAprobacion;
    }

    public void setPorcentajeAprobacion(Double porcentajeAprobacion) {
        this.porcentajeAprobacion = porcentajeAprobacion;
    }
}
