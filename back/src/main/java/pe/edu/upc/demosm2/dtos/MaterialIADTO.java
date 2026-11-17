package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;

public class MaterialIADTO {
    @NotBlank @Pattern(regexp="[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}")
    private String id;
    private String propietario;
    @NotBlank @Size(max=200)
    private String tema;
    @NotBlank @Size(max=60)
    private String nivel;
    @Size(max=1000)
    private String instrucciones;
    private String estado;
    private String titulo;
    private String texto;
    private String textoRevisado;
    public String getTextoRevisado() { return textoRevisado; }
    public void setTextoRevisado(String value) { this.textoRevisado = value; }
    private String mensaje;
    private String modeloTexto;
    private String modeloFiguras;
    private java.util.List<String> archivos;
    private long segundos;
    public String getId() { return id; }
    public void setId(String value) { this.id = value; }
    public String getPropietario() { return propietario; }
    public void setPropietario(String value) { this.propietario = value; }
    public String getTema() { return tema; }
    public void setTema(String value) { this.tema = value; }
    public String getNivel() { return nivel; }
    public void setNivel(String value) { this.nivel = value; }
    public String getInstrucciones() { return instrucciones; }
    public void setInstrucciones(String value) { this.instrucciones = value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { this.estado = value; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String value) { this.titulo = value; }
    public String getTexto() { return texto; }
    public void setTexto(String value) { this.texto = value; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String value) { this.mensaje = value; }
    public String getModeloTexto() { return modeloTexto; }
    public void setModeloTexto(String value) { this.modeloTexto = value; }
    public String getModeloFiguras() { return modeloFiguras; }
    public void setModeloFiguras(String value) { this.modeloFiguras = value; }
    public java.util.List<String> getArchivos() { return archivos; }
    public void setArchivos(java.util.List<String> value) { this.archivos = value; }
    public long getSegundos() { return segundos; }
    public void setSegundos(long value) { this.segundos = value; }
}
