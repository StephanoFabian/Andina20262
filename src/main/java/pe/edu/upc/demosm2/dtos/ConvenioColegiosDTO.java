package pe.edu.upc.demosm2.dtos;

import java.time.LocalDate;

public class ConvenioColegiosDTO {
    private Long idConvenio;

    private String nombre;

    private String entidad;

    private String tipoEntidad;

    private String region;

    private String estado;

    private LocalDate fechaFin;

    private Long cantidadColegios;

    public ConvenioColegiosDTO() {
    }

    public Long getIdConvenio() {
        return idConvenio;
    }

    public void setIdConvenio(Long idConvenio) {
        this.idConvenio = idConvenio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(String tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Long getCantidadColegios() {
        return cantidadColegios;
    }

    public void setCantidadColegios(Long cantidadColegios) {
        this.cantidadColegios = cantidadColegios;
    }
}
