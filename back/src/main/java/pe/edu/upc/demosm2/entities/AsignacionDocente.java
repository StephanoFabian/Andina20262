package pe.edu.upc.demosm2.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "AsignacionDocente")
public class AsignacionDocente {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_asignacion;
    @Column(name = "nombre_colegio",length = 200,nullable = false)
    private String id_aula;
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
    private Long horassemanales;
    @ManyToOne
    @JoinColumn(name = "id_colegio")
    private Colegio colegio;

public Long getId_asignacion() {
        return id_asignacion;
    }

    public void setId_asignacion(Long id_asignacion) {
        this.id_asignacion = id_asignacion;
    }

    public String getId_aula() {
        return id_aula;
    }

    public void setId_aula(String id_aula) {
        this.id_aula = id_aula;
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

    public Long getHorassemanales() {
        return horassemanales;
    }

    public void setHorassemanales(Long horassemanales) {
        this.horassemanales = horassemanales;
    }

    public Colegio getColegio() {
        return colegio;
    }

    public void setColegio(Colegio colegio) {
        this.colegio = colegio;
    }
    
}
