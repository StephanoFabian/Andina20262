package pe.edu.upc.demosm2.dtos;

import jakarta.validation.constraints.*;
import java.time.*;

public class HorarioDTO {

    private Long idHorario;
    @NotNull @Min(1) @Max(7)
    private Integer diaSemana;
    @NotNull
    private LocalTime horaInicio;
    @NotNull
    private LocalTime horaFin;
    @NotNull @Positive
    private Long idAsignacion;
    public Long getIdHorario() { return idHorario; }
    public void setIdHorario(Long value) { this.idHorario = value; }
    public Integer getDiaSemana() { return diaSemana; }
    public void setDiaSemana(Integer value) { this.diaSemana = value; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime value) { this.horaInicio = value; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime value) { this.horaFin = value; }
    public Long getIdAsignacion() { return idAsignacion; }
    public void setIdAsignacion(Long value) { this.idAsignacion = value; }
}
