package pe.edu.upc.demosm2.serviceinterfaces;

import java.util.List;
import java.util.Optional;
import pe.edu.upc.demosm2.entities.Convenio;

public interface IConvenioService {
    public List<Convenio> list();

    public Convenio insert(Convenio x);

    public Optional<Convenio> listId(Long id);

    public void update(Convenio x);

    public void delete(Long id);

    public List<Object[]> reportePorVencer(Integer dias);
}
