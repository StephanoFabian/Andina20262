package pe.edu.upc.demosm2.dtos;

public class CuentasPorRolDTO {
    private String rol;
    private Long total;
    private Long activos;
    private Long inactivos;
    private Long sinPassword;

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getActivos() {
        return activos;
    }

    public void setActivos(Long activos) {
        this.activos = activos;
    }

    public Long getInactivos() {
        return inactivos;
    }

    public void setInactivos(Long inactivos) {
        this.inactivos = inactivos;
    }

    public Long getSinPassword() {
        return sinPassword;
    }

    public void setSinPassword(Long sinPassword) {
        this.sinPassword = sinPassword;
    }
}
