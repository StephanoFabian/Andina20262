package pe.edu.upc.demosm2.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

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

    // HU06: conectividad medida (vacía hasta que se registre la primera medición)
    @Column(name = "velocidad_bajada_mbps")
    private Double velocidadBajadaMbps;

    @Column(name = "velocidad_subida_mbps")
    private Double velocidadSubidaMbps;

    @Column(name = "tipo_conexion", length = 30)
    private String tipoConexion;

    @Column(name = "fecha_medicion_conectividad")
    private LocalDate fechaMedicionConectividad;

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

    public Double getVelocidadBajadaMbps() {
        return velocidadBajadaMbps;
    }

    public void setVelocidadBajadaMbps(Double velocidadBajadaMbps) {
        this.velocidadBajadaMbps = velocidadBajadaMbps;
    }

    public Double getVelocidadSubidaMbps() {
        return velocidadSubidaMbps;
    }

    public void setVelocidadSubidaMbps(Double velocidadSubidaMbps) {
        this.velocidadSubidaMbps = velocidadSubidaMbps;
    }

    public String getTipoConexion() {
        return tipoConexion;
    }

    public void setTipoConexion(String tipoConexion) {
        this.tipoConexion = tipoConexion;
    }

    public LocalDate getFechaMedicionConectividad() {
        return fechaMedicionConectividad;
    }

    public void setFechaMedicionConectividad(LocalDate fechaMedicionConectividad) {
        this.fechaMedicionConectividad = fechaMedicionConectividad;
    }
}
