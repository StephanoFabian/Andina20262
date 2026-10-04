package com.andina.plataforma.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialRequestDTO {

    @NotNull(message = "El ID de la persona es obligatorio")
    private Integer idPersona;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String titulo;

    private String descripcion;

    @Size(max = 50, message = "El tipo no puede exceder 50 caracteres")
    private String tipo;

    @Size(max = 500, message = "La URL del archivo no puede exceder 500 caracteres")
    private String urlArchivo;

    private LocalDate fechaPublicacion;

    private List<Integer> idsCursos;
}