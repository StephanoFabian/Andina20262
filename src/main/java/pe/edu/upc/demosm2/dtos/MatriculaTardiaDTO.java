package pe.edu.upc.demosm2.dtos;

import java.time.LocalDate;

public class MatriculaTardiaDTO {
    private String periodo;

    private LocalDate fechaInicio;

    private Long totalMatriculas;

    private Long anticipadas;

    private Long tardias;

    private Long fueraDelPeriodo;

    private Double porcentajeTardias;

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Long getTotalMatriculas() {
        return totalMatriculas;
    }

    public void setTotalMatriculas(Long totalMatriculas) {
        this.totalMatriculas = totalMatriculas;
    }

    public Long getAnticipadas() {
        return anticipadas;
    }

    public void setAnticipadas(Long anticipadas) {
        this.anticipadas = anticipadas;
    }

    public Long getTardias() {
        return tardias;
    }

    public void setTardias(Long tardias) {
        this.tardias = tardias;
    }

    public Long getFueraDelPeriodo() {
        return fueraDelPeriodo;
    }

    public void setFueraDelPeriodo(Long fueraDelPeriodo) {
        this.fueraDelPeriodo = fueraDelPeriodo;
    }

    public Double getPorcentajeTardias() {
        return porcentajeTardias;
    }

    public void setPorcentajeTardias(Double porcentajeTardias) {
        this.porcentajeTardias = porcentajeTardias;
    }
}
