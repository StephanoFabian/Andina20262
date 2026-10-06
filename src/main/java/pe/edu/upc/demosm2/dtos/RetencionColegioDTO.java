package pe.edu.upc.demosm2.dtos;

public class RetencionColegioDTO {
    private String colegio;
    private Long alumnosAnterior;
    private Long alumnosActual;
    private Long siguieron;
    private Long seFueron;
    private Long nuevos;
    private Long variacion;

    public String getColegio() {
        return colegio;
    }

    public void setColegio(String colegio) {
        this.colegio = colegio;
    }

    public Long getAlumnosAnterior() {
        return alumnosAnterior;
    }

    public void setAlumnosAnterior(Long alumnosAnterior) {
        this.alumnosAnterior = alumnosAnterior;
    }

    public Long getAlumnosActual() {
        return alumnosActual;
    }

    public void setAlumnosActual(Long alumnosActual) {
        this.alumnosActual = alumnosActual;
    }

    public Long getSiguieron() {
        return siguieron;
    }

    public void setSiguieron(Long siguieron) {
        this.siguieron = siguieron;
    }

    public Long getSeFueron() {
        return seFueron;
    }

    public void setSeFueron(Long seFueron) {
        this.seFueron = seFueron;
    }

    public Long getNuevos() {
        return nuevos;
    }

    public void setNuevos(Long nuevos) {
        this.nuevos = nuevos;
    }

    public Long getVariacion() {
        return variacion;
    }

    public void setVariacion(Long variacion) {
        this.variacion = variacion;
    }
}
