package pe.edu.upc.demosm2.dtos;

import java.time.LocalTime;

public class HorarioInsertDTO {

    private Long id_horario;
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Long idAsignacion;

    public HorarioInsertDTO() {
    }

    public HorarioInsertDTO(Long id_horario, String diaSemana, LocalTime horaInicio, LocalTime horaFin, Long idAsignacion) {
        this.id_horario = id_horario;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.idAsignacion = idAsignacion;
    }


    public Long getId_horario() {
        return id_horario;
    }

    public void setId_horario(Long id_horario) {
        this.id_horario = id_horario;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public Long getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(Long idAsignacion) {
        this.idAsignacion = idAsignacion;
    }
}
