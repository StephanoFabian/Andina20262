package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;

public class MatriculaDTOInsert {
    private Long idMatricula;

    @NotNull(message = "idColegio es obligatorio")
    private Long idColegio;

    @NotNull(message = "idPersona es obligatorio")
    private Long idPersona;

    public Long getIdMatricula() {
        return idMatricula;
    }

    public void setIdMatricula(Long idMatricula) {
        this.idMatricula = idMatricula;
    }

    public Long getIdColegio() {
        return idColegio;
    }

    public void setIdColegio(Long idColegio) {
        this.idColegio = idColegio;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }
}
