package pe.edu.upc.demosm2.serviceimplements;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.demosm2.dtos.PaginaDTO;
import java.util.*;
@Service
@Transactional(readOnly=true)
public class ConsultaPaginadaService {
    private final EntityManager em;
    public ConsultaPaginadaService(EntityManager em) { this.em=em; }
    // Columnas y rutas son constantes internas; nunca provienen del cliente.
    public PaginaDTO<Map<String,Object>> pagina(Class<?> tipo, String[] columnas, String[] busqueda, String[] orden, int page, int size, String q) {
        return pagina(tipo,columnas,busqueda,orden,page,size,q,Map.of());
    }
    public PaginaDTO<Map<String,Object>> pagina(Class<?> tipo, String[] columnas, String[] busqueda, String[] orden, int page, int size, String q, Map<String,Object> filtros) {
        if(page<0 || page>10000 || size<1 || size>100 || q.length()>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100; búsqueda: hasta 100 caracteres");
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<Tuple> consulta=cb.createTupleQuery();
        Root<?> root=consulta.from(tipo);
        List<Selection<?>> seleccion=new ArrayList<>();
        for(String columna:columnas) seleccion.add(ruta(root,columna.split("=",2)[1]));
        consulta.multiselect(seleccion);
        consulta.where(filtro(cb,root,busqueda,q,filtros));
        consulta.orderBy(Arrays.stream(orden).map(o -> cb.asc(ruta(root,o))).toList());
        List<Map<String,Object>> filas=em.createQuery(consulta).setFirstResult(page*size).setMaxResults(size).setHint("jakarta.persistence.query.timeout",5000)
            .getResultList().stream().map(t -> {
                Map<String,Object> fila=new LinkedHashMap<>();
                for(int i=0;i<columnas.length;i++) fila.put(columnas[i].split("=",2)[0],t.get(i));
                return fila;
            }).toList();
        CriteriaQuery<Long> cuenta=cb.createQuery(Long.class);
        Root<?> cr=cuenta.from(tipo);
        cuenta.select(cb.count(cr)).where(filtro(cb,cr,busqueda,q,filtros));
        long total=em.createQuery(cuenta).setHint("jakarta.persistence.query.timeout",5000).getSingleResult();
        return new PaginaDTO<>(filas,total,page,size);
    }
    private Path<?> ruta(Path<?> root,String nombre) { for(String parte:nombre.split("\\.")) root=root.get(parte); return root; }
    private Predicate filtro(CriteriaBuilder cb,Root<?> root,String[] campos,String q,Map<String,Object> filtros) {
        List<Predicate> condiciones=new ArrayList<>();
        if(!q.isBlank()) {
            String texto=q.strip().toLowerCase(Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
            condiciones.add(cb.or(Arrays.stream(campos).map(c -> cb.like(cb.lower(ruta(root,c).as(String.class)),texto,'!')).toArray(Predicate[]::new)));
        }
        filtros.forEach((campo,valor) -> {
            if(campo.endsWith("<=")) condiciones.add(cb.lessThanOrEqualTo(ruta(root,campo.substring(0,campo.length()-2)).as(java.time.LocalDate.class),(java.time.LocalDate)valor));
            else if(valor instanceof Collection<?> valores) condiciones.add(ruta(root,campo).in(valores));
            else condiciones.add(cb.equal(ruta(root,campo),valor));
        });
        return cb.and(condiciones.toArray(Predicate[]::new));
    }
}
