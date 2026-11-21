package pe.edu.upc.demosm2.dtos;

public class EstudianteVariasMatriculasDTO {
    private Long idPersona;

    private String nombres;

    private String apellidos;

    private Long matriculas;

    private Long colegios;

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Long getMatriculas() {
        return matriculas;
    }

    public void setMatriculas(Long matriculas) {
        this.matriculas = matriculas;
    }

    public Long getColegios() {
        return colegios;
    }

    public void setColegios(Long colegios) {
        this.colegios = colegios;
    }
}
