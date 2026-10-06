package pe.edu.upc.demosm2.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

// HU13: módulo de capacitación en competencias digitales de un docente y su resultado.
@Entity
@Table(name = "capacitacion_docente")
public class CapacitacionDocente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_capacitacion")
    private Long idCapacitacion;

    // Docente evaluado (DOCENTE, ESPECIALISTA o LOCAL)
    @ManyToOne
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    @Column(name = "modulo", length = 100, nullable = false)
    private String modulo;

    // 0 a 20; vacío mientras el taller sigue en curso
    @Column(name = "puntaje")
    private Double puntaje;

    @Column(name = "fecha_evaluacion")
    private LocalDate fechaEvaluacion;

    // EN_CURSO, APROBADO o DESAPROBADO (se calcula con el puntaje: aprueba con 11)
    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    public CapacitacionDocente() {
    }

    public Long getIdCapacitacion() {
        return idCapacitacion;
    }

    public void setIdCapacitacion(Long idCapacitacion) {
        this.idCapacitacion = idCapacitacion;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public Double getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(Double puntaje) {
        this.puntaje = puntaje;
    }

    public LocalDate getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDate fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
