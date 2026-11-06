package pe.edu.upc.demosm2.serviceimplements;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.dtos.ConvenioDTO;
import pe.edu.upc.demosm2.repositories.IConvenioRepository;
import pe.edu.upc.demosm2.serviceinterfaces.IConvenioService;
import pe.edu.upc.demosm2.exceptions.ResourceNotFoundException;
@Service
@Transactional
public class ConvenioServiceImplement implements IConvenioService {
    private final IConvenioRepository repo;
    private final EntityManager em;
    public ConvenioServiceImplement(IConvenioRepository repo, EntityManager em) { this.repo=repo; this.em=em; }
    @Transactional(readOnly=true)
    public ConvenioDTO buscar(Long id) { return convertir(existente(id)); }
    private Convenio existente(Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe Convenio: " + id)); }
    public ConvenioDTO guardar(Long id, ConvenioDTO dto) {
        Convenio entidad = id == null ? new Convenio() : existente(id);
        Colegio colegio = em.find(Colegio.class, dto.getIdColegio());
        if (colegio == null) throw new ResourceNotFoundException("No existe colegio: " + dto.getIdColegio());
        entidad.setColegio(colegio);
        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) throw new IllegalArgumentException("La fecha final debe ser posterior o igual a la inicial");
        entidad.setNombre(dto.getNombre());
        entidad.setInstitucion(dto.getInstitucion());
        entidad.setFechaInicio(dto.getFechaInicio());
        entidad.setFechaFin(dto.getFechaFin());
        entidad.setEstado(dto.getEstado());
        return convertir(repo.saveAndFlush(entidad));
    }
    public void eliminar(Long id) { repo.delete(existente(id)); repo.flush(); }
    private ConvenioDTO convertir(Convenio e) {
        ConvenioDTO dto = new ConvenioDTO();
        dto.setIdConvenio(e.getIdConvenio());
        dto.setNombre(e.getNombre());
        dto.setInstitucion(e.getInstitucion());
        dto.setFechaInicio(e.getFechaInicio());
        dto.setFechaFin(e.getFechaFin());
        dto.setEstado(e.getEstado());
        dto.setIdColegio(e.getColegio().getIdColegio());
        return dto;
    }
}
