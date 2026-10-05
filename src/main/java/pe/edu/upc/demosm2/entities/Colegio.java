package pe.edu.upc.demosm2.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "colegios")
public class Colegio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idColegio;

    @Column(name = "nombre", length = 30, nullable = false)
    private String nombre;

    @Column(name = "departamento", length = 30, nullable = false)
    private String departamento;

    @Column(name = "provincia", length = 30, nullable = false)
    private String provincia;

    @Column(name = "distrito", length = 30, nullable = false)
    private String distrito;

    @Column(name = "comunidad", length = 30, nullable = false)
    private String comunidad;

    @Column(name = "tipo_zona", length = 30, nullable = false)
    private String tipo_zona;

    public Colegio(Long idColegio, String nombre, String departamento, String provincia, String distrito, String comunidad, String tipo_zona) {
        this.idColegio = idColegio;
        this.nombre = nombre;
        this.departamento = departamento;
        this.provincia = provincia;
        this.distrito = distrito;
        this.comunidad = comunidad;
        this.tipo_zona = tipo_zona;
    }

    public Colegio() {
    }

    public Long getIdColegio() {
        return idColegio;
    }

    public void setIdColegio(Long idColegio) {
        this.idColegio = idColegio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getComunidad() {
        return comunidad;
    }

    public void setComunidad(String comunidad) {
        this.comunidad = comunidad;
    }

    public String getTipo_zona() {
        return tipo_zona;
    }

    public void setTipo_zona(String tipo_zona) {
        this.tipo_zona = tipo_zona;
    }
}
