package com.andina.plataforma.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "material")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_material")
    @EqualsAndHashCode.Include
    private Integer idMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona", nullable = false)
    @ToString.Exclude
    private Persona persona;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Size(max = 50, message = "El tipo no puede exceder 50 caracteres")
    @Column(name = "tipo", length = 50)
    private String tipo;

    @Size(max = 500, message = "La URL del archivo no puede exceder 500 caracteres")
    @Column(name = "url_archivo", length = 500)
    private String urlArchivo;

    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "material_curso",
        joinColumns = @JoinColumn(name = "id_material"),
        inverseJoinColumns = @JoinColumn(name = "id_curso")
    )
    @ToString.Exclude
    @Builder.Default
    private Set<Curso> cursos = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}