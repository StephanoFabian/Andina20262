package pe.edu.upc.demosm2.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "PerfilAcademico")
public class PerfilAcademico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPerfilAcademico;

    @Column(name = "detallePA", length = 100, nullable = false)
    private String detallePA;

    @Column(name = "notasPA", nullable = false)
    private Double notasPA;

    public PerfilAcademico() {
    }

    public PerfilAcademico(Long idPerfilAcademico, String detallePA, Double notasPA) {
        this.idPerfilAcademico = idPerfilAcademico;
        this.detallePA = detallePA;
        this.notasPA = notasPA;
    }

    public Long getIdPerfilAcademico() {
        return idPerfilAcademico;
    }

    public void setIdPerfilAcademico(Long idPerfilAcademico) {
        this.idPerfilAcademico = idPerfilAcademico;
    }

    public String getDetallePA() {
        return detallePA;
    }

    public void setDetallePA(String detallePA) {
        this.detallePA = detallePA;
    }

    public Double getNotasPA() {
        return notasPA;
    }

    public void setNotasPA(Double notasPA) {
        this.notasPA = notasPA;
    }
}
