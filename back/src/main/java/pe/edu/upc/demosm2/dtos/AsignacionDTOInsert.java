package pe.edu.upc.demosm2.dtos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import pe.edu.upc.demosm2.entities.Curso;

public class AsignacionDTOInsert {
private Long id_asignacion;
    @NotBlank(message = "Esto no puede estar vacio")
    private String id_aula;
    @Positive(message = "Indique el curso a dictar")
    private Long id_curso;
    @Positive(message = "Indique el periodo de estudio")
    private Long id_periodo;
    @Positive(message = "Identifique a la persona")
    private Long id_persona;
    @NotBlank(message = "Especifique la modalidad de estudio")
    private String modalidad;
    @NotNull(message = "Ingrese la cantidad de horas de estudio")
    private Long horassemanales;
    @Positive(message = "Indique el colegio en asignar")
    private Long id_colegio;

    public AsignacionDTOInsert() {
    }

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

    public Long getId_curso() {
        return id_curso;
    }

    public void setId_curso(Long id_curso) {
        this.id_curso = id_curso;
    }

    public Long getId_periodo() {
        return id_periodo;
    }

    public void setId_periodo(Long id_periodo) {
        this.id_periodo = id_periodo;
    }

    public Long getId_persona() {
        return id_persona;
    }

    public void setId_persona(Long id_persona) {
        this.id_persona = id_persona;
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

    public Long getId_colegio() {
        return id_colegio;
    }

    public void setId_colegio(Long id_colegio) {
        this.id_colegio = id_colegio;
    }
}
