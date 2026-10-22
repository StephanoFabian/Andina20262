package pe.edu.upc.demosm2.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// HU14: ticket de soporte técnico con prioridad y estado.
@Entity
@Table(name = "incidencia")
public class Incidencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_incidencia")
    private Long idIncidencia;

    // Quién reporta (sale del token, no del cuerpo)
    @ManyToOne
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    // Escuela afectada; opcional
    @ManyToOne
    @JoinColumn(name = "id_colegio")
    private Colegio colegio;

    @Column(name = "titulo", length = 150, nullable = false)
    private String titulo;

    @Column(name = "descripcion", length = 1000, nullable = false)
    private String descripcion;

    // BAJA, MEDIA, ALTA o CRITICA
    @Column(name = "prioridad", length = 20, nullable = false)
    private String prioridad;

    // ABIERTA, EN_PROCESO, RESUELTA o CERRADA
    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    // true si la falla afecta una sesión de clase en curso
    @Column(name = "afecta_sesion", nullable = false)
    private boolean afectaSesion;

    @Column(name = "fecha_reporte", nullable = false)
    private LocalDateTime fechaReporte;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    public Incidencia() {
    }

    public Long getIdIncidencia() {
        return idIncidencia;
    }

    public void setIdIncidencia(Long idIncidencia) {
        this.idIncidencia = idIncidencia;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Colegio getColegio() {
        return colegio;
    }

    public void setColegio(Colegio colegio) {
        this.colegio = colegio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isAfectaSesion() {
        return afectaSesion;
    }

    public void setAfectaSesion(boolean afectaSesion) {
        this.afectaSesion = afectaSesion;
    }

    public LocalDateTime getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(LocalDateTime fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }
}
