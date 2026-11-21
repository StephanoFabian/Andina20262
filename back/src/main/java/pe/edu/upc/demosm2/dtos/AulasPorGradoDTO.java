package pe.edu.upc.demosm2.dtos;

public class AulasPorGradoDTO {
    private String grado;

    private String nivel;

    private Long vigentes;

    private Long aulasNecesarias;

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public Long getVigentes() {
        return vigentes;
    }

    public void setVigentes(Long vigentes) {
        this.vigentes = vigentes;
    }

    public Long getAulasNecesarias() {
        return aulasNecesarias;
    }

    public void setAulasNecesarias(Long aulasNecesarias) {
        this.aulasNecesarias = aulasNecesarias;
    }
}
