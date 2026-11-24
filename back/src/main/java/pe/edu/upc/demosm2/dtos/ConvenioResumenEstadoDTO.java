package pe.edu.upc.demosm2.dtos;

import java.math.BigDecimal;

/** Dashboard de convenios: cuántos convenios hay en cada estado y su presupuesto total */
public class ConvenioResumenEstadoDTO {
    private String estado;
    private Long cantidad;
    private BigDecimal presupuesto;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(BigDecimal presupuesto) {
        this.presupuesto = presupuesto;
    }
}
