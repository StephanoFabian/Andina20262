package pe.edu.upc.demosm2.servicesinterfaces;

import pe.edu.upc.demosm2.entities.PerfilAcademico;

import java.util.List;
import java.util.Optional;

public interface IPerfilAcademicoService {
    public List<PerfilAcademico> list();
    public PerfilAcademico insert(PerfilAcademico pa);
    public Optional<PerfilAcademico> listId(Long id);
    public void update(PerfilAcademico pa);
    public void delete(Long id);
}
