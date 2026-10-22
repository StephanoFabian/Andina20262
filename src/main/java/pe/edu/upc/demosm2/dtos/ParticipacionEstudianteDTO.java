package pe.edu.upc.demosm2.dtos;

public class ParticipacionEstudianteDTO {
    private Long idPersona;

    private String nombres;

    private String apellidos;

    private Long sesiones;

    private Long asistencias;

    private Long faltas;

    private Long justificadas;

    private Double porcentajeAsistencia;

    private Double promedioCalificacion;

    private Boolean riesgoDesercion;

    public ParticipacionEstudianteDTO() {
    }

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

    public Long getSesiones() {
        return sesiones;
    }

    public void setSesiones(Long sesiones) {
        this.sesiones = sesiones;
    }

    public Long getAsistencias() {
        return asistencias;
    }

    public void setAsistencias(Long asistencias) {
        this.asistencias = asistencias;
    }

    public Long getFaltas() {
        return faltas;
    }

    public void setFaltas(Long faltas) {
        this.faltas = faltas;
    }

    public Long getJustificadas() {
        return justificadas;
    }

    public void setJustificadas(Long justificadas) {
        this.justificadas = justificadas;
    }

    public Double getPorcentajeAsistencia() {
        return porcentajeAsistencia;
    }

    public void setPorcentajeAsistencia(Double porcentajeAsistencia) {
        this.porcentajeAsistencia = porcentajeAsistencia;
    }

    public Double getPromedioCalificacion() {
        return promedioCalificacion;
    }

    public void setPromedioCalificacion(Double promedioCalificacion) {
        this.promedioCalificacion = promedioCalificacion;
    }

    public Boolean getRiesgoDesercion() {
        return riesgoDesercion;
    }

    public void setRiesgoDesercion(Boolean riesgoDesercion) {
        this.riesgoDesercion = riesgoDesercion;
    }
}
