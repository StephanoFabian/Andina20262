package pe.edu.upc.demosm2.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// HU16: respuesta de una persona a una encuesta (una sola vez por encuesta).
@Entity
@Table(name = "respuesta_encuesta", uniqueConstraints = @UniqueConstraint(columnNames = {"id_encuesta", "id_persona"}))
public class RespuestaEncuesta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_respuesta")
    private Long idRespuesta;

    @ManyToOne
    @JoinColumn(name = "id_encuesta", nullable = false)
    private Encuesta encuesta;

    // Quién responde (sale del token); su rol da el perfil de la respuesta
    @ManyToOne
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    // Escala Likert: 1 = muy insatisfecho … 5 = muy satisfecho
    @Column(name = "satisfaccion_general", nullable = false)
    private Integer satisfaccionGeneral;

    @Column(name = "calidad_contenido", nullable = false)
    private Integer calidadContenido;

    @Column(name = "facilidad_uso", nullable = false)
    private Integer facilidadUso;

    // Comentario abierto, opcional
    @Column(name = "comentario", length = 500)
    private String comentario;

    @Column(name = "fecha_respuesta", nullable = false)
    private LocalDateTime fechaRespuesta;

    public RespuestaEncuesta() {
    }

    public Long getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(Long idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    public Encuesta getEncuesta() {
        return encuesta;
    }

    public void setEncuesta(Encuesta encuesta) {
        this.encuesta = encuesta;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Integer getSatisfaccionGeneral() {
        return satisfaccionGeneral;
    }

    public void setSatisfaccionGeneral(Integer satisfaccionGeneral) {
        this.satisfaccionGeneral = satisfaccionGeneral;
    }

    public Integer getCalidadContenido() {
        return calidadContenido;
    }

    public void setCalidadContenido(Integer calidadContenido) {
        this.calidadContenido = calidadContenido;
    }

    public Integer getFacilidadUso() {
        return facilidadUso;
    }

    public void setFacilidadUso(Integer facilidadUso) {
        this.facilidadUso = facilidadUso;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }
}
