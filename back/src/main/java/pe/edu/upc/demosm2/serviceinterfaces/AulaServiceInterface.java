package pe.edu.upc.demosm2.serviceinterfaces;
import pe.edu.upc.demosm2.entities.Aula;

import java.util.List;
import java.util.Optional;

public interface AulaServiceInterface {

    public List<Aula> list();
    public void insert(Aula a);
    public Optional<Aula> listId(Long id);
    public void delete(Long id);
    java.util.List<Object[]> pagina(String texto,int limite,int desplazamiento);
    long contarPagina(String texto);
}
