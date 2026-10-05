package pe.edu.upc.demosm2.dtos;

import java.time.LocalDateTime;
import java.util.List;

// HU46: confirmación de la actualización parcial (campos cambiados y marca de tiempo).
public class PerfilAcademicoParcialRespuestaDTO {
    private Long idPerfilAcademico;

    private Long idPersona;

    private List<String> camposModificados;

    private LocalDateTime fechaActualizacion;

    public PerfilAcademicoParcialRespuestaDTO() {
    }

    public Long getIdPerfilAcademico() {
        return idPerfilAcademico;
    }

    public void setIdPerfilAcademico(Long idPerfilAcademico) {
        this.idPerfilAcademico = idPerfilAcademico;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public List<String> getCamposModificados() {
        return camposModificados;
    }

    public void setCamposModificados(List<String> camposModificados) {
        this.camposModificados = camposModificados;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
