package com.andina.plataforma.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialResponseDTO {

    private Integer idMaterial;
    private Integer idPersona;
    private String nombrePersona;
    private String titulo;
    private String descripcion;
    private String tipo;
    private String urlArchivo;
    private LocalDate fechaPublicacion;
    private List<CursoResumenDTO> cursos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}