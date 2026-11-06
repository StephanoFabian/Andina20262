package pe.edu.upc.demosm2.dtos;
import java.util.List;
public record PaginaDTO<T>(List<T> content, long totalElements, int number, int size) { }
