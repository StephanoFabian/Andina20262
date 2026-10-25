package pe.edu.upc.demosm2.dtos;

public class MaterialPorTipoDTO {
    private String tipo;
    private Long cantidad;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }
}
