package pe.edu.upc.demosm2.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "aulas")
public class Aula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAula;

    @Column(name = "nombre", length = 30, nullable = false)
    private String nombre;

    @Column(name = "seccion", length = 30, nullable = false)
    private String seccion;

    @Column(name = "capacidad", length = 30, nullable = false)
    private int capacidad;

    @ManyToOne
    @JoinColumn(name = "idColegio",nullable = false)
    private Colegio colegio;

    public Aula() {
    }

    public Colegio getColegio() {
        return colegio;
    }

    public void setColegio(Colegio colegio) {
        this.colegio = colegio;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getIdAula() {
        return idAula;
    }

    public void setIdAula(Long idAula) {
        this.idAula = idAula;
    }
}
