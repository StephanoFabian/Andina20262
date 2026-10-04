package pe.edu.upc.demosm2.dtos;

// Alumnos vigentes por grado en un periodo y aulas necesarias (40 por aula)
// Resultado del query nativo: cada getter recibe la columna con el mismo alias (AS grado, ...).
// El controller lo pasa al DTO con ModelMapper.
public interface AulasPorGradoQuery {
    String getGrado();
    String getNivel();
    Long getVigentes();
    Long getAulasNecesarias();
}
