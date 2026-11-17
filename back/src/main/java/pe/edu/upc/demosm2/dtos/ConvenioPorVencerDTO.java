package pe.edu.upc.demosm2.dtos;
public class ConvenioPorVencerDTO {
    private Long idConvenio;
    public Long getIdConvenio() {return idConvenio;}
    public void setIdConvenio(Long value) {this.idConvenio=value;}
    private String nombre;
    public String getNombre() {return nombre;}
    public void setNombre(String value) {this.nombre=value;}
    private String entidad;
    public String getEntidad() {return entidad;}
    public void setEntidad(String value) {this.entidad=value;}
    private String region;
    public String getRegion() {return region;}
    public void setRegion(String value) {this.region=value;}
    private java.time.LocalDate fechaFin;
    public java.time.LocalDate getFechaFin() {return fechaFin;}
    public void setFechaFin(java.time.LocalDate value) {this.fechaFin=value;}
    private java.math.BigDecimal presupuesto;
    public java.math.BigDecimal getPresupuesto() {return presupuesto;}
    public void setPresupuesto(java.math.BigDecimal value) {this.presupuesto=value;}
    private String estado;
    public String getEstado() {return estado;}
    public void setEstado(String value) {this.estado=value;}
    private Long diasRestantes;
    public Long getDiasRestantes() {return diasRestantes;}
    public void setDiasRestantes(Long value) {this.diasRestantes=value;}
    private String situacion;
    public String getSituacion() {return situacion;}
    public void setSituacion(String value) {this.situacion=value;}
}
