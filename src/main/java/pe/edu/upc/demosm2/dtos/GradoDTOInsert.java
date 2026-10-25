package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;

public class GradoDTOInsert {
    private Long idGrado;

    @NotBlank(message = "nombre es obligatorio")
    @Size(max = 50, message = "nombre admite como máximo 50 caracteres")
    private String nombre;

    @NotBlank(message = "nivel es obligatorio")
    @Size(max = 50, message = "nivel admite como máximo 50 caracteres")
    private String nivel;

    public Long getIdGrado() {
        return idGrado;
    }

    public void setIdGrado(Long idGrado) {
        this.idGrado = idGrado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }
}
