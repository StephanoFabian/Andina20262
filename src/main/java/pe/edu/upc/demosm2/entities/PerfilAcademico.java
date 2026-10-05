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

    // HU33/HU39: el perfil pertenece a un estudiante (una persona tiene un solo perfil)
    @OneToOne
    @JoinColumn(name = "idPersona", unique = true)
    private Persona persona;

    // HU39/HU46: dato confidencial; solo lo consultan los roles autorizados
    @Column(name = "estadoPsicologico", length = 100)
    private String estadoPsicologico;

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

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getEstadoPsicologico() {
        return estadoPsicologico;
    }

    public void setEstadoPsicologico(String estadoPsicologico) {
        this.estadoPsicologico = estadoPsicologico;
    }
}
