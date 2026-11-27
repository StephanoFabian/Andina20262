package pe.edu.upc.demosm2.dtos;

import java.math.BigDecimal;

/** Dashboard de convenios: convenios vigentes o en renovación que vencen en cada mes (yyyy-MM) */
public class ConvenioVencimientoMesDTO {
    private String mes;
    private Long cantidad;
    private BigDecimal presupuesto;

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
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
