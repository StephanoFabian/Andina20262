package pe.edu.upc.demosm2.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// HU15: bitácora de accesos y cambios. Solo se inserta: la API no expone ni modificación ni borrado.
@Entity
@Table(name = "auditoria")
public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long idAuditoria;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    // ID de la persona (en el login, el ID con el que se intentó entrar)
    @Column(name = "usuario", length = 50)
    private String usuario;

    @Column(name = "modulo", length = 50, nullable = false)
    private String modulo;

    // LOGIN_OK, LOGIN_FALLIDO, LOGIN_INACTIVO, CREAR, ACTUALIZAR, ELIMINAR o CAMBIO_PASSWORD
    @Column(name = "accion", length = 30, nullable = false)
    private String accion;

    @Column(name = "detalle", length = 300)
    private String detalle;

    @Column(name = "ip", length = 45)
    private String ip;

    @Column(name = "estado_http")
    private Integer estadoHttp;

    public Auditoria() {
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
