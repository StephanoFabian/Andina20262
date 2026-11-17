package pe.edu.upc.demosm2.entities;

import jakarta.persistence.*;
import java.time.*;
@Entity
@Table(name="convenios", indexes={@Index(name="ix_convenios_idColegio", columnList="idColegio")})
public class Convenio {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idConvenio;
    @Column(length=200, nullable=false)
    private String nombre;
    @Column(length=200, nullable=false)
    private String institucion;
    @Column(nullable=false)
    private LocalDate fechaInicio;
    @Column(nullable=false)
    private LocalDate fechaFin;
    @Column(length=200, nullable=false)
    private String estado;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="idColegio")
    private Colegio colegio;
    public Long getIdConvenio() { return idConvenio; }
    public void setIdConvenio(Long value) { this.idConvenio = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public String getInstitucion() { return institucion; }
    public void setInstitucion(String value) { this.institucion = value; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate value) { this.fechaInicio = value; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate value) { this.fechaFin = value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { this.estado = value; }
    public Colegio getColegio() { return colegio; }
    public void setColegio(Colegio value) { this.colegio = value; }
    @Column(length=50)
    private String tipoEntidad;
    @Column(length=50)
    private String region;
    @Column(precision=14,scale=2)
    private java.math.BigDecimal presupuesto;
    public String getTipoEntidad() { return tipoEntidad; }
    public void setTipoEntidad(String value) { tipoEntidad=value; }
    public String getRegion() { return region; }
    public void setRegion(String value) { region=value; }
    public java.math.BigDecimal getPresupuesto() { return presupuesto; }
    public void setPresupuesto(java.math.BigDecimal value) { presupuesto=value; }
}
