package pe.edu.upc.demosm2.dtos;

// Cuántas personas tiene cada rol y qué porcentaje del total son
// Resultado del query nativo: cada getter recibe la columna con el mismo alias (AS rol, ...).
// El controller lo pasa al DTO con ModelMapper.
public interface RolCantidadQuery {
    String getRol();
    Long getCantidad();
    Double getPorcentaje();
}
