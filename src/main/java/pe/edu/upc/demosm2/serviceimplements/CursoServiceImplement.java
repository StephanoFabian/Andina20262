package pe.edu.upc.demosm2.serviceimplements;

import org.springframework.stereotype.Service;
import pe.edu.upc.demosm2.entities.Curso;
import pe.edu.upc.demosm2.repositories.ICursoRepository;
import pe.edu.upc.demosm2.serviceinterfaces.ICursoService;

import java.util.List;
import java.util.Optional;

@Service
public class CursoServiceImplement implements ICursoService {

    public final ICursoRepository qR;

    public CursoServiceImplement(ICursoRepository qR) {
        this.qR = qR;
    }

    @Override
    public void insert(Curso q) {
        qR.save(q);
    }

    @Override
    public List<Curso> list() {
        return qR.findAll();
    }

    @Override
    public Optional<Curso> listId(Long id) {
        return qR.findById(id);
    }

    @Override
    public void update(Curso q) {
        qR.save(q);
    }

    @Override
    public void delete(Long id) {
        qR.deleteById(id);
    }

    @Override
    public List<Curso> buscarporArea(String area) {
        return qR.findByArea(area);
    }
}
