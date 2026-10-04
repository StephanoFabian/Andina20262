package pe.edu.upc.demosm2.dtos;

// Población que gana o pierde cada colegio entre dos periodos
// Resultado del query nativo: cada getter recibe la columna con el mismo alias (AS colegio, ...).
// El controller lo pasa al DTO con ModelMapper.
public interface RetencionColegioQuery {
    String getColegio();
    Long getAlumnosAnterior();
    Long getAlumnosActual();
    Long getSiguieron();
    Long getSeFueron();
    Long getNuevos();
    Long getVariacion();
}
