package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class PersonaRegistroDTO {
    private Long idPersona;

    @NotBlank(message = "nombresPersona es obligatorio")
    @Size(max = 150, message = "nombresPersona admite como máximo 150 caracteres")
    private String nombresPersona;

    @NotBlank(message = "apellidosPersona es obligatorio")
    @Size(max = 150, message = "apellidosPersona admite como máximo 150 caracteres")
    private String apellidosPersona;

    @NotNull(message = "fechaNacimientoPersona es obligatoria")
    @Past(message = "fechaNacimientoPersona debe ser una fecha pasada")
    private LocalDate fechaNacimientoPersona;

    @NotBlank(message = "emailPersona es obligatorio")
    @Email(message = "emailPersona no tiene un formato válido")
    @Size(max = 150, message = "emailPersona admite como máximo 150 caracteres")
    private String emailPersona;

    // Si no se envía, la persona queda ACTIVO (HU45)
    @Size(max = 100, message = "estadoPersona admite como máximo 100 caracteres")
    private String estadoPersona;

    // Obligatoria al registrar. Al actualizar, si va vacía se conserva la anterior
    @Size(max = 72, message = "passwordPersona admite como máximo 72 caracteres")
    private String passwordPersona;

    @NotNull(message = "idRol es obligatorio")
    private Long idRol;

    // Opcional: aula de la persona (debe existir)
    private Long idAula;

    public PersonaRegistroDTO() {
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombresPersona() {
        return nombresPersona;
    }

    public void setNombresPersona(String nombresPersona) {
        this.nombresPersona = nombresPersona;
    }

    public String getApellidosPersona() {
        return apellidosPersona;
    }

    public void setApellidosPersona(String apellidosPersona) {
        this.apellidosPersona = apellidosPersona;
    }

    public LocalDate getFechaNacimientoPersona() {
        return fechaNacimientoPersona;
    }

    public void setFechaNacimientoPersona(LocalDate fechaNacimientoPersona) {
        this.fechaNacimientoPersona = fechaNacimientoPersona;
    }

    public String getEmailPersona() {
        return emailPersona;
    }

    public void setEmailPersona(String emailPersona) {
        this.emailPersona = emailPersona;
    }

    public String getEstadoPersona() {
        return estadoPersona;
    }

    public void setEstadoPersona(String estadoPersona) {
        this.estadoPersona = estadoPersona;
    }

    public String getPasswordPersona() {
        return passwordPersona;
    }

    public void setPasswordPersona(String passwordPersona) {
        this.passwordPersona = passwordPersona;
    }

    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }

    public Long getIdAula() {
        return idAula;
    }

    public void setIdAula(Long idAula) {
        this.idAula = idAula;
    }
}
