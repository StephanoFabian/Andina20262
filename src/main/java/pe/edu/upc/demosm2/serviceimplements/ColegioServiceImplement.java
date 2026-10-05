package pe.edu.upc.demosm2.serviceimplements;

import pe.edu.upc.demosm2.entities.Colegio;
import pe.edu.upc.demosm2.repositories.IColegioRepository;
import pe.edu.upc.demosm2.serviceinterfaces.ColegioServiceInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@Service
public class ColegioServiceImplement implements ColegioServiceInterface {

    public final IColegioRepository ICR;

    public ColegioServiceImplement(IColegioRepository ICR) {
        this.ICR = ICR;
    }

    @Override
    public Slice<Colegio> list(Pageable pageable){return ICR.findAllBy(pageable);}
    @Override
    public void insert(Colegio c){ICR.save(c);}
    @Override
    public Optional<Colegio> listId(Long id){return ICR.findById(id);}
    @Override
    public void delete(Long id){ICR.deleteById(id);}


}
