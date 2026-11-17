package pe.edu.upc.demosm2.entities;

import jakarta.persistence.*;
import java.time.*;
@Entity
@Table(name="asistencias", indexes={@Index(name="ix_asistencias_idSesionVirtual", columnList="idSesionVirtual"),@Index(name="ix_asistencias_idPersona", columnList="idPersona")}, uniqueConstraints=@UniqueConstraint(name="uk_asistencia_sesion_persona", columnNames={"idSesionVirtual","idPersona"}))
public class Asistencia {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idAsistencia;
    @Column(length=150, nullable=false)
    private String estado;
    @Column(length=500, nullable=true)
    private String observacion;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="idSesionVirtual", nullable=false)
    private SesionVirtual sesionVirtual;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="idPersona", nullable=false)
    private Persona persona;
    public Long getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(Long value) { this.idAsistencia = value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { this.estado = value; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String value) { this.observacion = value; }
    public SesionVirtual getSesionVirtual() { return sesionVirtual; }
    public void setSesionVirtual(SesionVirtual value) { this.sesionVirtual = value; }
    public Persona getPersona() { return persona; }
    public void setPersona(Persona value) { this.persona = value; }
}
