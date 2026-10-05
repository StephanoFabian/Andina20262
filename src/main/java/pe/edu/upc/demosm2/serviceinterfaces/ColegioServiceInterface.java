package pe.edu.upc.demosm2.serviceinterfaces;
import pe.edu.upc.demosm2.entities.Colegio;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ColegioServiceInterface {

    Slice<Colegio> list(Pageable pageable);
    public void insert(Colegio c);
    public Optional<Colegio> listId(Long id);
    public void delete(Long id);

    // HU06: true si la conectividad medida alcanza el mínimo para clases virtuales (sin medición = no cumple)
    public boolean cumpleConectividadMinima(Colegio c);

    public double getBajadaMinimaMbps();

    public double getSubidaMinimaMbps();

    public List<Object[]> reporteConectividad();
}
