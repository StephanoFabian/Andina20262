package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.NotBlank;

public class AulaDTOInsert {

    private Long idAula;

    @NotBlank(message = "El nombre es obligatorio!!!")
    private String nombre;

    @NotBlank(message = "La seccion es obligatoria!!!")
    private String seccion;

    @NotBlank(message = "La capacidad es obligatoria!!!")
    private int capacidad;

    @NotBlank(message = "El id del colegio es obligatorio!!!")
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
