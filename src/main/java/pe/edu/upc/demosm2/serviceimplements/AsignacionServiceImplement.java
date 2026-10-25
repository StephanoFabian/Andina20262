package pe.edu.upc.demosm2.serviceimplements;

import org.springframework.stereotype.Service;
import pe.edu.upc.demosm2.dtos.AsignacionDTOList;
import pe.edu.upc.demosm2.entities.AsignacionDocente;
import pe.edu.upc.demosm2.repositories.IAsignacionDocenteRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IAsignacionDocenteService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class AsignacionServiceImplement implements IAsignacionDocenteService {

    public final IAsignacionDocenteRepository aR;

    public AsignacionServiceImplement(IAsignacionDocenteRepository aR) {
        this.aR = aR;
    }

    @Override
    public void insert(AsignacionDocente d) {
        aR.save(d);
    }

    @Override
    public List<AsignacionDocente> list() {
        return aR.findAll();
    }

    @Override
    public Optional<AsignacionDocente> listId(Long id) {
        return aR.findById(id);
    }

    @Override
    public void update(AsignacionDocente aD) {
        aR.save(aD);
    }

    @Override
    public void delete(Long id) {
        aR.deleteById(id);
    }

    @Override
    public List<AsignacionDocente> ObtenerPorRangoHoras(Long hora_min, Long hora_max) {
        return aR.findByHorassemanalesIsBetween(hora_min,hora_max)
                .stream()
                .sorted(Comparator.comparing(AsignacionDocente::getId_asignacion))
                .toList();
    }

    @Override
    public long contarConflictos(Long idColegio, Long idCurso, Long idPeriodo, String aula, Long excluir) {
        return aR.contarConflictos(idColegio, idCurso, idPeriodo, aula, excluir);
    }
}
