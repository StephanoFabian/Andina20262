package pe.edu.upc.demosm2.dtos;

// Cuántos materiales hay de cada tipo
// Resultado del query nativo: cada getter recibe la columna con el mismo alias (AS tipo, ...).
// El controller lo pasa al DTO con ModelMapper.
public interface MaterialPorTipoQuery {
    String getTipo();
    Long getCantidad();
}
