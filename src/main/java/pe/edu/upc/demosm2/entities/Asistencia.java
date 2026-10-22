package pe.edu.upc.demosm2.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

// HU08: asistencia y calificación de un estudiante en una sesión de un curso.
// Un alumno tiene un solo registro por curso y fecha de sesión.
@Entity
@Table(name = "asistencia", uniqueConstraints = @UniqueConstraint(columnNames = {"id_persona", "id_curso", "fecha_sesion"}))
public class Asistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia")
    private Long idAsistencia;

    // Estudiante que asiste a la sesión
    @ManyToOne
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false)
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "id_periodo", nullable = false)
    private PeriodoAcademico periodo;

    @Column(name = "fecha_sesion", nullable = false)
    private LocalDate fechaSesion;

    // PRESENTE, TARDANZA, AUSENTE o JUSTIFICADO
    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    // Nota de la sesión (0 a 20); opcional
    @Column(name = "calificacion")
    private Double calificacion;

    @Column(name = "observacion", length = 200)
    private String observacion;

    public Asistencia() {
    }

    public Long getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(Long idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public PeriodoAcademico getPeriodo() {
        return periodo;
    }

    public void setPeriodo(PeriodoAcademico periodo) {
        this.periodo = periodo;
    }

    public LocalDate getFechaSesion() {
        return fechaSesion;
    }

    public void setFechaSesion(LocalDate fechaSesion) {
        this.fechaSesion = fechaSesion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
