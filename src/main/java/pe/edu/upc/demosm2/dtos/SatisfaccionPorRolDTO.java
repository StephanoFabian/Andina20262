package pe.edu.upc.demosm2.dtos;

public class SatisfaccionPorRolDTO {
    private String rol;

    private Long respuestas;

    private Double promedioSatisfaccion;

    private Double promedioContenido;

    private Double promedioFacilidad;

    private Double porcentajeSatisfechos;

    public SatisfaccionPorRolDTO() {
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Long getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(Long respuestas) {
        this.respuestas = respuestas;
    }

    public Double getPromedioSatisfaccion() {
        return promedioSatisfaccion;
    }

    public void setPromedioSatisfaccion(Double promedioSatisfaccion) {
        this.promedioSatisfaccion = promedioSatisfaccion;
    }

    public Double getPromedioContenido() {
        return promedioContenido;
    }

    public void setPromedioContenido(Double promedioContenido) {
        this.promedioContenido = promedioContenido;
    }

    public Double getPromedioFacilidad() {
        return promedioFacilidad;
    }

    public void setPromedioFacilidad(Double promedioFacilidad) {
        this.promedioFacilidad = promedioFacilidad;
    }

    public Double getPorcentajeSatisfechos() {
        return porcentajeSatisfechos;
    }

    public void setPorcentajeSatisfechos(Double porcentajeSatisfechos) {
        this.porcentajeSatisfechos = porcentajeSatisfechos;
    }
}
