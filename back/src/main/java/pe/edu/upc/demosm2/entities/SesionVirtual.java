package pe.edu.upc.demosm2.entities;

import jakarta.persistence.*;
import java.time.*;
@Entity
@Table(name="sesiones_virtuales", indexes={@Index(name="ix_sesiones_virtuales_idAsignacion", columnList="idAsignacion")})
public class SesionVirtual {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idSesionVirtual;
    @Column(length=150, nullable=false)
    private String titulo;
    @Column(nullable=false)
    private LocalDate fecha;
    @Column(nullable=false)
    private LocalTime horaInicio;
    @Column(nullable=false)
    private LocalTime horaFin;
    @Column(length=500, nullable=false)
    private String enlace;
    @Column(length=150, nullable=false)
    private String estado;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="idAsignacion", nullable=false)
    private AsignacionDocente asignacion;
    public Long getIdSesionVirtual() { return idSesionVirtual; }
    public void setIdSesionVirtual(Long value) { this.idSesionVirtual = value; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String value) { this.titulo = value; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate value) { this.fecha = value; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime value) { this.horaInicio = value; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime value) { this.horaFin = value; }
    public String getEnlace() { return enlace; }
    public void setEnlace(String value) { this.enlace = value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { this.estado = value; }
    public AsignacionDocente getAsignacion() { return asignacion; }
    public void setAsignacion(AsignacionDocente value) { this.asignacion = value; }
}
