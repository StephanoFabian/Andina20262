package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;

// HU44: cuerpo de la reasignación rápida de aula.
public class PersonaAulaDTO {
    @NotNull(message = "idAula es obligatorio")
    @Positive(message = "idAula debe ser positivo")
    private Long idAula;

    public PersonaAulaDTO() {
    }

    public Long getIdAula() {
        return idAula;
    }

    public void setIdAula(Long idAula) {
        this.idAula = idAula;
    }
}
