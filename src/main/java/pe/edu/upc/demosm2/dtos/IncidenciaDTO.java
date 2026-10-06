package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class IncidenciaDTO {
    private Long idIncidencia;

    // Salida: quien reporta (se toma del token)
    private Long idPersona;

    // Opcional
    private Long idColegio;

    @NotBlank(message = "titulo es obligatorio")
    @Size(max = 150, message = "titulo admite como máximo 150 caracteres")
    private String titulo;

    @NotBlank(message = "descripcion es obligatorio")
    @Size(max = 1000, message = "descripcion admite como máximo 1000 caracteres")
    private String descripcion;

    @NotBlank(message = "prioridad es obligatorio")
    @Pattern(regexp = "(?i)BAJA|MEDIA|ALTA|CRITICA", message = "prioridad debe ser BAJA, MEDIA, ALTA o CRITICA")
    private String prioridad;

    // Salida: ABIERTA al registrar; se cambia con PATCH /incidencias/{id}/estado
    private String estado;

    // true si afecta una sesión de clase en curso
    private Boolean afectaSesion;

    private LocalDateTime fechaReporte;

    private LocalDateTime fechaCierre;

    public IncidenciaDTO() {
    }

    public Long getIdIncidencia() {
        return idIncidencia;
    }

    public void setIdIncidencia(Long idIncidencia) {
        this.idIncidencia = idIncidencia;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public Long getIdColegio() {
        return idColegio;
    }

    public void setIdColegio(Long idColegio) {
        this.idColegio = idColegio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getAfectaSesion() {
        return afectaSesion;
    }

    public void setAfectaSesion(Boolean afectaSesion) {
        this.afectaSesion = afectaSesion;
    }

    public LocalDateTime getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(LocalDateTime fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }
}
