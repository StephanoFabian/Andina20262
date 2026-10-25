package pe.edu.upc.demosm2.serviceimplements;

import org.springframework.stereotype.Service;
import pe.edu.upc.demosm2.entities.Grado;
import pe.edu.upc.demosm2.repositories.IGradoRepository;
import pe.edu.upc.demosm2.serviceinterfaces.GradoServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class GradoServiceImplement implements GradoServiceInterface {
    private final IGradoRepository repository;

    public GradoServiceImplement(IGradoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Grado> list() {
        return repository.findAll();
    }

    @Override
    public void insert(Grado grado) {
        repository.save(grado);
    }

    @Override
    public Optional<Grado> listId(Long id) {
        return repository.findById(id);
    }

    @Override
    public void update(Grado grado) {
        repository.save(grado);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
