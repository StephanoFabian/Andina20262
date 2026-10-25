package pe.edu.upc.demosm2.dtos;

import java.time.LocalDateTime;

public class AuditoriaDTO {
    private Long idAuditoria;

    private LocalDateTime fechaHora;

    private String usuario;

    private String modulo;

    private String accion;

    private String detalle;

    private String ip;

    private Integer estadoHttp;

    public AuditoriaDTO() {
    }

    public Long getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(Long idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getEstadoHttp() {
        return estadoHttp;
    }

    public void setEstadoHttp(Integer estadoHttp) {
        this.estadoHttp = estadoHttp;
    }
}
