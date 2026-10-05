package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.NotBlank;

public class ColegioDTOInsert {


    private Long idColegio;

    @NotBlank(message = "El nombre es obligatorio!!!")
    private String nombre;

    @NotBlank(message = "El departamento es obligatorio!!!")
    private String departamento;

    @NotBlank(message = "La provincia es obligatoria!!!")
    private String provincia;

    @NotBlank(message = "El distrito es obligatorio!!!")
    private String distrito;

    @NotBlank(message = "La comunidad es obligatoria!!!")
    private String comunidad;

    @NotBlank(message = "El tipo de zona es obligatorio!!!")
    private String tipo_zona;

    public Long getIdColegio() {
        return idColegio;
    }

    public void setIdColegio(Long idColegio) {
        this.idColegio = idColegio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getComunidad() {
        return comunidad;
    }

    public void setComunidad(String comunidad) {
        this.comunidad = comunidad;
    }

    public String getTipo_zona() {
        return tipo_zona;
    }

    public void setTipo_zona(String tipo_zona) {
        this.tipo_zona = tipo_zona;
    }
}
