package pe.edu.upc.demosm2.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Rol")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoPersona;

    @Column(name = "detalleRol", length = 50, nullable = false)
    private String detalle;

    public Rol() {
    }

    public Rol(Long idTipoPersona, String detalle) {
        this.idTipoPersona = idTipoPersona;
        this.detalle = detalle;
    }

    public Long getIdTipoPersona() {
        return idTipoPersona;
    }

    public void setIdTipoPersona(Long idTipoPersona) {
        this.idTipoPersona = idTipoPersona;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
