package pe.edu.upc.demosm2.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

// HU11: convenio con un gobierno regional o una entidad de cooperación (vigencia, región y presupuesto).
@Entity
@Table(name = "convenio")
public class Convenio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_convenio")
    private Long idConvenio;

    @Column(name = "nombre", length = 200, nullable = false)
    private String nombre;

    // Gobierno regional u organismo de cooperación
    @Column(name = "entidad", length = 200, nullable = false)
    private String entidad;

    // GOBIERNO_REGIONAL, COOPERACION_INTERNACIONAL u OTRO
    @Column(name = "tipo_entidad", length = 50, nullable = false)
    private String tipoEntidad;

    @Column(name = "region", length = 50, nullable = false)
    private String region;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "presupuesto", precision = 14, scale = 2, nullable = false)
    private BigDecimal presupuesto;

    // VIGENTE, EN_RENOVACION, VENCIDO o FINALIZADO
    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    public Convenio() {
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

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public BigDecimal getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(BigDecimal presupuesto) {
        this.presupuesto = presupuesto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
