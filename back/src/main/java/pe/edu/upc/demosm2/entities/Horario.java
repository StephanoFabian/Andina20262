package pe.edu.upc.demosm2.entities;

import jakarta.persistence.*;
import java.time.*;
@Entity
@Table(name="horarios", uniqueConstraints=@UniqueConstraint(name="uk_horario_asignacion_dia_horas",columnNames={"idAsignacion","diaSemana","horaInicio","horaFin"}), indexes={@Index(name="ix_horarios_cruce", columnList="idAsignacion,diaSemana,horaInicio,horaFin"),@Index(name="ix_horarios_idAsignacion", columnList="idAsignacion")})
public class Horario {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idHorario;
    @Column(nullable=false)
    private Integer diaSemana;
    @Column(nullable=false)
    private LocalTime horaInicio;
    @Column(nullable=false)
    private LocalTime horaFin;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="idAsignacion", nullable=false)
    private AsignacionDocente asignacion;
    public Long getIdHorario() { return idHorario; }
    public void setIdHorario(Long value) { this.idHorario = value; }
    public Integer getDiaSemana() { return diaSemana; }
    public void setDiaSemana(Integer value) { this.diaSemana = value; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime value) { this.horaInicio = value; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime value) { this.horaFin = value; }
    public AsignacionDocente getAsignacion() { return asignacion; }
    public void setAsignacion(AsignacionDocente value) { this.asignacion = value; }
}
