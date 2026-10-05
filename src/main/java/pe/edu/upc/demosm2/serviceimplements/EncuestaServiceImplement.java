package pe.edu.upc.demosm2.serviceimplements;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.demosm2.entities.Encuesta;
import pe.edu.upc.demosm2.repositories.IEncuestaRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IEncuestaService;

@Service
public class EncuestaServiceImplement implements IEncuestaService {

    @Autowired
    private IEncuestaRepository eR;

    @Override
    public List<Encuesta> list() {
        return eR.findAll();
    }

    @Override
    public Encuesta insert(Encuesta x) {
        return eR.save(x);
    }

    @Override
    public Optional<Encuesta> listId(Long id) {
        return eR.findById(id);
    }

    @Override
    public void update(Encuesta x) {
        eR.save(x);
    }

    @Override
    public void delete(Long id) {
        eR.deleteById(id);
    }

    @Override
    public List<Object[]> reporteParticipacion() {
        return eR.reporteParticipacion();
    }
}
