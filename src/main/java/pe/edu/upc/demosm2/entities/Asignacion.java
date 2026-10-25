package pe.edu.upc.demosm2.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "AsignacionDocente")
public class Asignacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_asignacion;
    @ManyToOne
    @JoinColumn(name = "id_aula")
    private Aula aula;
    @ManyToOne
    @JoinColumn(name = "id_curso")
    private Curso curso;
    @ManyToOne
    @JoinColumn(name = "id_periodo")
    private PeriodoAcademico periodoAcademico;
    @ManyToOne
    @JoinColumn(name = "id_persona")
    private Persona persona;
    @Column(name = "modalidad",length = 150,nullable = false)
    private String modalidad;
    @Column(name = "horas_semanales",nullable = false)
    @JsonProperty("horassemanales")
    private Long horasSemanales;
    @ManyToOne
    @JoinColumn(name = "id_colegio")
    private Colegio colegio;

public Long getId_asignacion() {
        return id_asignacion;
    }

    public void setId_asignacion(Long id_asignacion) {
        this.id_asignacion = id_asignacion;
    }

    public Aula getAula() {
        return aula;
    }

    public void setAula(Aula aula) {
        this.aula = aula;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public PeriodoAcademico getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public Long getHorasSemanales() {
        return horasSemanales;
    }

    public void setHorasSemanales(Long horasSemanales) {
        this.horasSemanales = horasSemanales;
    }

    public Colegio getColegio() {
        return colegio;
    }

    public void setColegio(Colegio colegio) {
        this.colegio = colegio;
    }
    
}
