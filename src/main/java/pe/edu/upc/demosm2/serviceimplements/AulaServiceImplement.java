package pe.edu.upc.demosm2.serviceimplements;

import pe.edu.upc.demosm2.entities.Aula;
import pe.edu.upc.demosm2.repositories.IAulaRepository;
import pe.edu.upc.demosm2.serviceinterfaces.AulaServiceInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AulaServiceImplement implements AulaServiceInterface {

    private final IAulaRepository IAR;
    public AulaServiceImplement(IAulaRepository iar){IAR =iar;}

    @Override
    public List<Aula> list(){return IAR.findAll();}

    @Override
    public void insert(Aula a){IAR.save(a);}

    @Override
    public Optional<Aula> listId(Long id){return IAR.findById(id);}

    @Override
    public void delete(Long id){IAR.deleteById(id);}
}
