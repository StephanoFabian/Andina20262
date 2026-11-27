package pe.edu.upc.demosm2.dtos;

import java.time.LocalDate;

public class EvolucionPeriodoDTO {
    private String periodo;

    private LocalDate fechaInicio;

    private String estado;

    private Long matriculados;

    private Long periodoAnterior;

    private Double variacionPorcentual;

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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getMatriculados() {
        return matriculados;
    }

    public void setMatriculados(Long matriculados) {
        this.matriculados = matriculados;
    }

    public Long getPeriodoAnterior() {
        return periodoAnterior;
    }

    public void setPeriodoAnterior(Long periodoAnterior) {
        this.periodoAnterior = periodoAnterior;
    }

    public Double getVariacionPorcentual() {
        return variacionPorcentual;
    }

    public void setVariacionPorcentual(Double variacionPorcentual) {
        this.variacionPorcentual = variacionPorcentual;
    }
}
