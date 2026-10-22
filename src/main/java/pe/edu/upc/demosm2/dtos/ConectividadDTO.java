package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

// HU06: conectividad medida de una escuela.
public class ConectividadDTO {
    // Salida
    private Long idColegio;

    @NotNull(message = "velocidadBajadaMbps es obligatoria")
    @PositiveOrZero(message = "velocidadBajadaMbps no puede ser negativa")
    private Double velocidadBajadaMbps;

    @NotNull(message = "velocidadSubidaMbps es obligatoria")
    @PositiveOrZero(message = "velocidadSubidaMbps no puede ser negativa")
    private Double velocidadSubidaMbps;

    // FIBRA, ADSL, 4G, SATELITAL, RADIOENLACE…
    @NotBlank(message = "tipoConexion es obligatorio")
    @Size(max = 30, message = "tipoConexion admite como máximo 30 caracteres")
    private String tipoConexion;

    // Si no se envía, se usa la fecha de hoy
    @PastOrPresent(message = "fechaMedicion no puede ser futura")
    private LocalDate fechaMedicion;

    // Salida: true si alcanza el mínimo para clases virtuales
    private Boolean cumpleMinimo;

    // Salida: umbral configurado
    private Double bajadaMinimaMbps;

    // Salida: umbral configurado
    private Double subidaMinimaMbps;

    public ConectividadDTO() {
    }

    public Long getIdColegio() {
        return idColegio;
    }

    public void setIdColegio(Long idColegio) {
        this.idColegio = idColegio;
    }

    public Double getVelocidadBajadaMbps() {
        return velocidadBajadaMbps;
    }

    public void setVelocidadBajadaMbps(Double velocidadBajadaMbps) {
        this.velocidadBajadaMbps = velocidadBajadaMbps;
    }

    public Double getVelocidadSubidaMbps() {
        return velocidadSubidaMbps;
    }

    public void setVelocidadSubidaMbps(Double velocidadSubidaMbps) {
        this.velocidadSubidaMbps = velocidadSubidaMbps;
    }

    public String getTipoConexion() {
        return tipoConexion;
    }

    public void setTipoConexion(String tipoConexion) {
        this.tipoConexion = tipoConexion;
    }

    public LocalDate getFechaMedicion() {
        return fechaMedicion;
    }

    public void setFechaMedicion(LocalDate fechaMedicion) {
        this.fechaMedicion = fechaMedicion;
    }

    public Boolean getCumpleMinimo() {
        return cumpleMinimo;
    }

    public void setCumpleMinimo(Boolean cumpleMinimo) {
        this.cumpleMinimo = cumpleMinimo;
    }

    public Double getBajadaMinimaMbps() {
        return bajadaMinimaMbps;
    }

    public void setBajadaMinimaMbps(Double bajadaMinimaMbps) {
        this.bajadaMinimaMbps = bajadaMinimaMbps;
    }

    public Double getSubidaMinimaMbps() {
        return subidaMinimaMbps;
    }

    public void setSubidaMinimaMbps(Double subidaMinimaMbps) {
        this.subidaMinimaMbps = subidaMinimaMbps;
    }
}
