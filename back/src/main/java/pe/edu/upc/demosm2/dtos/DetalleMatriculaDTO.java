package pe.edu.upc.demosm2.dtos;

import java.time.LocalDate;

public class DetalleMatriculaDTO {
    private Long idDetalleMatricula;
    private LocalDate fechaMatricula;
    private String estado;
    private Long idMatricula;
    private Long idCurso;
    private Long idPeriodo;
    private Long idGrado;

    public Long getIdDetalleMatricula() {
        return idDetalleMatricula;
    }

    public void setIdDetalleMatricula(Long idDetalleMatricula) {
        this.idDetalleMatricula = idDetalleMatricula;
    }

    public LocalDate getFechaMatricula() {
        return fechaMatricula;
    }

    public void setFechaMatricula(LocalDate fechaMatricula) {
        this.fechaMatricula = fechaMatricula;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getIdMatricula() {
        return idMatricula;
    }

    public void setIdMatricula(Long idMatricula) {
        this.idMatricula = idMatricula;
    }

    public Long getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Long idCurso) {
        this.idCurso = idCurso;
    }

    public Long getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(Long idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public Long getIdGrado() {
        return idGrado;
    }

    public void setIdGrado(Long idGrado) {
        this.idGrado = idGrado;
    }
}
