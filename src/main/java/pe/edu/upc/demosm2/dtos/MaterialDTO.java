package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class MaterialDTO {
    private Long idMaterial;

    @NotBlank(message = "titulo es obligatorio")
    @Size(max = 200, message = "titulo admite como máximo 200 caracteres")
    private String titulo;

    private String descripcion;

    @Size(max = 50, message = "tipo admite como máximo 50 caracteres")
    private String tipo;

    @Size(max = 500, message = "urlArchivo admite como máximo 500 caracteres")
    @Pattern(regexp = "^https?://\\S+$", message = "urlArchivo debe ser un enlace http:// o https:// sin espacios")
    private String urlArchivo;

    private LocalDate fechaPublicacion;

    @NotNull(message = "idPersona (autor) es obligatorio")
    private Long idPersona;

    public MaterialDTO() {
    }

    public Long getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Long idMaterial) {
        this.idMaterial = idMaterial;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUrlArchivo() {
        return urlArchivo;
    }

    public void setUrlArchivo(String urlArchivo) {
        this.urlArchivo = urlArchivo;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }
}
