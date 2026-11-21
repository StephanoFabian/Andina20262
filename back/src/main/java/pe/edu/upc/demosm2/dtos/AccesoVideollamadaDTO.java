package pe.edu.upc.demosm2.dtos;

public class AccesoVideollamadaDTO {
    private String url;
    private String token;
    private String sala;
    private String identidad;
    private String titulo;
    private boolean traduccionDisponible;
    public String getUrl() { return url; }
    public void setUrl(String valor) { this.url = valor; }
    public String getToken() { return token; }
    public void setToken(String valor) { this.token = valor; }
    public String getSala() { return sala; }
    public void setSala(String valor) { this.sala = valor; }
    public String getIdentidad() { return identidad; }
    public void setIdentidad(String valor) { this.identidad = valor; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String valor) { this.titulo = valor; }
    public boolean getTraduccionDisponible() { return traduccionDisponible; }
    public void setTraduccionDisponible(boolean valor) { this.traduccionDisponible = valor; }
}
