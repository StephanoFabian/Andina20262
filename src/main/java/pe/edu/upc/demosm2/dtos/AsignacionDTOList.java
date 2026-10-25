package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class AsignacionDTOList {
    private Long id_asignacion;
    private String id_aula;
    private Long id_curso;
    private String modalidad;
    private Long horassemanales;
    private Long id_persona;
    private Long id_periodo;
    private Long id_colegio;

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

    public Long getId_asignacion() {
        return id_asignacion;
    }

    public void setId_asignacion(Long id_asignacion) {
        this.id_asignacion = id_asignacion;
    }

    public Long getId_persona() {
        return id_persona;
    }

    public void setId_persona(Long id_persona) {
        this.id_persona = id_persona;
    }

    public Long getId_periodo() {
        return id_periodo;
    }

    public void setId_periodo(Long id_periodo) {
        this.id_periodo = id_periodo;
    }

    public Long getId_colegio() {
        return id_colegio;
    }

    public void setId_colegio(Long id_colegio) {
        this.id_colegio = id_colegio;
    }
}
