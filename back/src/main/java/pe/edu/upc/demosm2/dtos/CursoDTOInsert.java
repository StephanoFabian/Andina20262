package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.NotBlank;

public class CursoDTOInsert {
    private Long id_curso;
    @NotBlank(message = "El nombre del curso es obligatorio")
    private String nombre_curso;
    @NotBlank(message = "Ingrese una descripcion")
    private String descripcion;
    @NotBlank(message = "Indique el area que pertenece")
    private String area;

    public Long getId_curso() {
        return id_curso;
    }

    public void setId_curso(Long id_curso) {
        this.id_curso = id_curso;
    }

    public String getNombre_curso() {
        return nombre_curso;
    }

    public void setNombre_curso(String nombre_curso) {
        this.nombre_curso = nombre_curso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
