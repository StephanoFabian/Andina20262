package pe.edu.upc.demosm2.dtos;

public class IncidenciasPorPrioridadDTO {
    private String prioridad;

    private Long total;

    private Long pendientes;

    private Long resueltas;

    private Double horasPromedioResolucion;

    public IncidenciasPorPrioridadDTO() {
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPendientes() {
        return pendientes;
    }

    public void setPendientes(Long pendientes) {
        this.pendientes = pendientes;
    }

    public Long getResueltas() {
        return resueltas;
    }

    public void setResueltas(Long resueltas) {
        this.resueltas = resueltas;
    }

    public Double getHorasPromedioResolucion() {
        return horasPromedioResolucion;
    }

    public void setHorasPromedioResolucion(Double horasPromedioResolucion) {
        this.horasPromedioResolucion = horasPromedioResolucion;
    }
}
