package pe.edu.upc.demosm2.dtos;

public class ConectividadColegioDTO {
    private Long idColegio;

    private String colegio;

    private String departamento;

    private String tipoZona;

    private String tipoConexion;

    private Double velocidadBajadaMbps;

    private Double velocidadSubidaMbps;

    private Long estudiantes;

    // SIN_MEDICION, INSUFICIENTE o ADECUADA
    private String estado;

    public ConectividadColegioDTO() {
    }

    public Long getIdColegio() {
        return idColegio;
    }

    public void setIdColegio(Long idColegio) {
        this.idColegio = idColegio;
    }

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

    public String getTipoConexion() {
        return tipoConexion;
    }

    public void setTipoConexion(String tipoConexion) {
        this.tipoConexion = tipoConexion;
    }

    public Double getVelocidadBajadaMbps() {
        return velocidadBajadaMbps;
    }

    public void setVelocidadBajadaMbps(Double velocidadBajadaMbps) {
        this.velocidadBajadaMbps = velocidadBajadaMbps;
    }

    public Double getVelocidadSubidaMbps() {
        return velocidadSubidaMbps;
    }

    public void setVelocidadSubidaMbps(Double velocidadSubidaMbps) {
        this.velocidadSubidaMbps = velocidadSubidaMbps;
    }

    public Long getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(Long estudiantes) {
        this.estudiantes = estudiantes;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
