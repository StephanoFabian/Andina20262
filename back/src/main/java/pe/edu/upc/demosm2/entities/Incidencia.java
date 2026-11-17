package pe.edu.upc.demosm2.entities;

import jakarta.persistence.*;
import java.time.*;
@Entity
@Table(name="incidencias", indexes={@Index(name="ix_incidencias_idPersona", columnList="idPersona"),@Index(name="ix_incidencias_idColegio", columnList="idColegio")})
public class Incidencia {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idIncidencia;
    @Column(length=150, nullable=false)
    private String titulo;
    @Column(length=2000, nullable=false)
    private String descripcion;
    @Column(nullable=false)
    private LocalDate fecha;
    @Column(length=150, nullable=false)
    private String estado;
    @Column(length=150, nullable=false)
    private String prioridad;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="idPersona", nullable=false)
    private Persona persona;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="idColegio", nullable=false)
    private Colegio colegio;
    public Long getIdIncidencia() { return idIncidencia; }
    public void setIdIncidencia(Long value) { this.idIncidencia = value; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String value) { this.titulo = value; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String value) { this.descripcion = value; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate value) { this.fecha = value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { this.estado = value; }
    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String value) { this.prioridad = value; }
    public Persona getPersona() { return persona; }
    public void setPersona(Persona value) { this.persona = value; }
    public Colegio getColegio() { return colegio; }
    public void setColegio(Colegio value) { this.colegio = value; }
}
