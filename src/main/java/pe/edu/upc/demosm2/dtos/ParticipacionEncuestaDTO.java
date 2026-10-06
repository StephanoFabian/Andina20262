package pe.edu.upc.demosm2.dtos;

public class ParticipacionEncuestaDTO {
    private Long idEncuesta;

    private String titulo;

    private String estado;

    private Long respuestas;

    private Double promedioGeneral;

    private Double porcentajeSatisfechos;

    public ParticipacionEncuestaDTO() {
    }

    public Long getIdEncuesta() {
        return idEncuesta;
    }

    public void setIdEncuesta(Long idEncuesta) {
        this.idEncuesta = idEncuesta;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(Long respuestas) {
        this.respuestas = respuestas;
    }

    public Double getPromedioGeneral() {
        return promedioGeneral;
    }

    public void setPromedioGeneral(Double promedioGeneral) {
        this.promedioGeneral = promedioGeneral;
    }

    public Double getPorcentajeSatisfechos() {
        return porcentajeSatisfechos;
    }

    public void setPorcentajeSatisfechos(Double porcentajeSatisfechos) {
        this.porcentajeSatisfechos = porcentajeSatisfechos;
    }
}
