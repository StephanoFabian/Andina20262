package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonAlias;

public class AulaDTOInsert {

    private Long idAula;

    @NotBlank(message = "El nombre es obligatorio!!!")
    @Size(max = 30)
    @JsonAlias("numero")
    private String nombre;

    @NotBlank(message = "La seccion es obligatoria!!!")
    @Size(max = 30)
    private String seccion;

    @Positive(message = "La capacidad debe ser mayor que cero")
    private int capacidad;

    @NotNull(message = "El colegio es obligatorio")
    @Positive(message = "El id del colegio debe ser positivo")
    private Long idColegio;

    public Long getIdAula() {
        return idAula;
    }

    public void setIdAula(Long idAula) {
        this.idAula = idAula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public Long getIdColegio() {
        return idColegio;
    }

    public void setIdColegio(Long idColegio) {
        this.idColegio = idColegio;
    }
}
