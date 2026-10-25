package pe.edu.upc.demosm2.dtos;

import java.time.LocalDateTime;

public class LoginFallidoDTO {
    private String usuario;

    private Long intentosFallidos;

    private LocalDateTime ultimoIntento;

    private Long ipsDistintas;

    public LoginFallidoDTO() {
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Long getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(Long intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public LocalDateTime getUltimoIntento() {
        return ultimoIntento;
    }

    public void setUltimoIntento(LocalDateTime ultimoIntento) {
        this.ultimoIntento = ultimoIntento;
    }

    public Long getIpsDistintas() {
        return ipsDistintas;
    }

    public void setIpsDistintas(Long ipsDistintas) {
        this.ipsDistintas = ipsDistintas;
    }
}
