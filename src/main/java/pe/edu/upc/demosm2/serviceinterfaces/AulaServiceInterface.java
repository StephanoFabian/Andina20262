package pe.edu.upc.demosm2.serviceinterfaces;
import pe.edu.upc.demosm2.entities.Aula;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface AulaServiceInterface {

    Slice<Aula> list(Long idColegio, Pageable pageable);
    public void insert(Aula a);
    public Optional<Aula> listId(Long id);
    public void delete(Long id);
}
