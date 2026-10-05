package pe.edu.upc.demosm2.serviceimplements;

import pe.edu.upc.demosm2.entities.Colegio;
import pe.edu.upc.demosm2.repositories.IColegioRepository;
import pe.edu.upc.demosm2.serviceinterfaces.ColegioServiceInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ColegioServiceImplement implements ColegioServiceInterface {

    public final IColegioRepository ICR;

    public ColegioServiceImplement(IColegioRepository ICR) {
        this.ICR = ICR;
    }

    @Override
    public List<Colegio> list(){return ICR.findAll();}
    @Override
    public void insert(Colegio c){ICR.save(c);}
    @Override
    public Optional<Colegio> listId(Long id){return ICR.findById(id);}
    @Override
    public void delete(Long id){ICR.deleteById(id);}


}
