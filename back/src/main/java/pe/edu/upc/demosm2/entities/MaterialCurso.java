package pe.edu.upc.demosm2.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "material_curso")
public class MaterialCurso {
    @EmbeddedId
    private MaterialCursoId id = new MaterialCursoId();

    @ManyToOne
    @MapsId("idMaterial")
    @JoinColumn(name = "id_material")
    private Material material;

    @ManyToOne
    @MapsId("idCurso")
    @JoinColumn(name = "id_curso")
    private Curso curso;

    public MaterialCurso() {
    }

    public MaterialCurso(Material material, Curso curso) {
        this.material = material;
        this.curso = curso;
        this.id = new MaterialCursoId(material.getIdMaterial(), curso.getId_curso());
    }

    public MaterialCursoId getId() {
        return id;
    }

    public void setId(MaterialCursoId id) {
        this.id = id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }
}
