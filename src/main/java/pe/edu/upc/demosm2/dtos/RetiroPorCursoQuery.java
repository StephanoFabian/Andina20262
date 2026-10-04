package pe.edu.upc.demosm2.dtos;

// Cursos que más alumnos pierden (retirados + trasladados)
// Resultado del query nativo: cada getter recibe la columna con el mismo alias (AS curso, ...).
// El controller lo pasa al DTO con ModelMapper.
public interface RetiroPorCursoQuery {
    String getCurso();
    String getArea();
    Long getTotalMatriculados();
    Long getRetirados();
    Long getTrasladados();
    Double getPorcentajePerdida();
}
