package pe.edu.upc.demosm2.dtos;

// Cuántos materiales tiene asignados cada curso
// Resultado del query nativo: cada getter recibe la columna con el mismo alias (AS curso, ...).
// El controller lo pasa al DTO con ModelMapper.
public interface CursoMaterialesQuery {
    String getCurso();
    Long getCantidadMateriales();
}
