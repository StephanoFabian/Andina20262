package pe.edu.upc.demosm2.serviceimplements;

import org.springframework.stereotype.Service;
import pe.edu.upc.demosm2.entities.Horario;
import pe.edu.upc.demosm2.repositories.IHorarioRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IHorarioService;

import java.util.List;
import java.util.Optional;

@Service
public class HorarioServiceImplement implements IHorarioService {
    private final IHorarioRepository hR;

    public HorarioServiceImplement(IHorarioRepository hR) {
        this.hR = hR;
    }


    @Override
    public void insert(Horario h) {
        hR.save(h);
    }

    @Override
    public List<Horario> list() {
        return hR.findAll();
    }

    @Override
    public Optional<Horario> listId(Long id) {
        return hR.findById(id);
    }

    @Override
    public void update(Horario h) {
        hR.save(h);
    }

    @Override
    public void delete(Long id) {
            hR.deleteById(id);
    }

    @Override
    public List<Horario> obtenerHorarioPorPersona(Long idPersona) {
        return hR.findHorarioByPersonaId(idPersona);
    }
}
