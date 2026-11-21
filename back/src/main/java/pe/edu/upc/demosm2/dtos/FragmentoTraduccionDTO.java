package pe.edu.upc.demosm2.dtos;

public class FragmentoTraduccionDTO {
    private String id;
    private String propietario;
    private String estado;
    private String idiomaOrigen;
    private String idiomaDestino;
    private String audioBase64;
    private String transcripcion;
    private String traduccion;
    private String mensaje;
    private long milisegundos;
    public String getId() { return id; }
    public void setId(String valor) { this.id = valor; }
    public String getPropietario() { return propietario; }
    public void setPropietario(String valor) { this.propietario = valor; }
    public String getEstado() { return estado; }
    public void setEstado(String valor) { this.estado = valor; }
    public String getIdiomaOrigen() { return idiomaOrigen; }
    public void setIdiomaOrigen(String valor) { this.idiomaOrigen = valor; }
    public String getIdiomaDestino() { return idiomaDestino; }
    public void setIdiomaDestino(String valor) { this.idiomaDestino = valor; }
    public String getAudioBase64() { return audioBase64; }
    public void setAudioBase64(String valor) { this.audioBase64 = valor; }
    public String getTranscripcion() { return transcripcion; }
    public void setTranscripcion(String valor) { this.transcripcion = valor; }
    public String getTraduccion() { return traduccion; }
    public void setTraduccion(String valor) { this.traduccion = valor; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String valor) { this.mensaje = valor; }
    public long getMilisegundos() { return milisegundos; }
    public void setMilisegundos(long valor) { this.milisegundos = valor; }
}
