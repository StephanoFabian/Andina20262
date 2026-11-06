package pe.edu.upc.demosm2.controllers;
import jakarta.persistence.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.dtos.PaginaDTO;
import pe.edu.upc.demosm2.serviceimplements.ConsultaPaginadaService;
import java.util.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
@RestController
@Transactional(readOnly=true)
public class RecursosConsultaController {
    private final ConsultaPaginadaService paginas;
    private final EntityManager em;
    public RecursosConsultaController(ConsultaPaginadaService paginas,EntityManager em) {this.paginas=paginas;this.em=em;}
    @GetMapping("/materiales/tipos")
    public List<?> tipos(@RequestParam(defaultValue="") String q) {
        if(q.length()>100)throw new IllegalArgumentException("Búsqueda demasiado larga");
        String texto=q.strip().toLowerCase(Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_")+"%";
        return em.createNativeQuery("select distinct tipo from material where tipo is not null and lower(tipo) like :texto escape '!' order by tipo").setParameter("texto",texto).setMaxResults(20).setHint("jakarta.persistence.query.timeout",5000).getResultList();
    }
    @GetMapping("/materiales/consulta/pagina")
    public PaginaDTO<Map<String,Object>> materiales(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q,@RequestParam(required=false) Long idCurso) {
        if(idCurso!=null && idCurso<1) throw new IllegalArgumentException("El curso debe ser positivo");
        return paginas.pagina(idCurso==null ? Material.class : MaterialCurso.class,
            idCurso==null ? new String[]{"idMaterial=idMaterial","titulo=titulo","descripcion=descripcion","tipo=tipo","urlArchivo=urlArchivo","fechaPublicacion=fechaPublicacion","idPersona=persona.idPersona"} : new String[]{"idMaterial=material.idMaterial","titulo=material.titulo","descripcion=material.descripcion","tipo=material.tipo","urlArchivo=material.urlArchivo","fechaPublicacion=material.fechaPublicacion","idPersona=material.persona.idPersona"},
            new String[]{idCurso==null ? "titulo" : "material.titulo"},new String[]{idCurso==null ? "idMaterial" : "id.idMaterial"},page,size,q,idCurso==null ? Map.of() : Map.of("id.idCurso",idCurso));
    }
    @GetMapping("/api/convenios/por-vencer/pagina")
    public PaginaDTO<Map<String,Object>> convenios(@RequestParam(defaultValue="90") int dias,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size) {
        if(dias<0 || dias>3650) throw new IllegalArgumentException("Plazo permitido: 0 a 3650 días");
        LocalDate hoy=LocalDate.now();
        var datos=paginas.pagina(Convenio.class,new String[]{"idConvenio=idConvenio","nombre=nombre","entidad=institucion","region=region","fechaFin=fechaFin","presupuesto=presupuesto","estado=estado"},new String[]{"nombre"},new String[]{"fechaFin","idConvenio"},page,size,"",Map.of("fechaFin<=",hoy.plusDays(dias),"estado",List.of("VIGENTE","EN_RENOVACION","VENCIDO")));
        for(var fila:datos.content()) {
            long restantes=ChronoUnit.DAYS.between(hoy,(LocalDate)fila.get("fechaFin"));
            fila.put("diasRestantes",restantes);fila.put("situacion",restantes<0 ? "Vencido" : "Por vencer");
        }
        return datos;
    }
    @GetMapping("/materiales/reporte-por-tipo/pagina")
    public PaginaDTO<Map<String,Object>> tipos(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size) {
        validar(page,size);
        List<?> filas=em.createNativeQuery("select tipo,count(*) from material group by tipo order by tipo nulls first").setFirstResult(page*size).setMaxResults(size).setHint("jakarta.persistence.query.timeout",5000).getResultList();
        long total=((Number)em.createNativeQuery("select count(*) from (select tipo from material group by tipo) t").setHint("jakarta.persistence.query.timeout",5000).getSingleResult()).longValue();
        return reporte(filas,new String[]{"tipo","cantidad"},total,page,size);
    }
    @GetMapping("/materiales-cursos/reporte-materiales-por-curso/pagina")
    public PaginaDTO<Map<String,Object>> cursos(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size) {
        validar(page,size);
        List<?> filas=em.createNativeQuery("select c.nombre_curso,count(mc.id_material) from curso c left join material_curso mc on mc.id_curso=c.id_curso group by c.id_curso,c.nombre_curso order by count(mc.id_material),c.id_curso").setFirstResult(page*size).setMaxResults(size).setHint("jakarta.persistence.query.timeout",5000).getResultList();
        long total=((Number)em.createNativeQuery("select count(*) from curso").setHint("jakarta.persistence.query.timeout",5000).getSingleResult()).longValue();
        return reporte(filas,new String[]{"curso","cantidadMateriales"},total,page,size);
    }
    private void validar(int page,int size) {if(page<0 || page>10000 || size<1 || size>100) throw new IllegalArgumentException("Página: 0–10000; tamaño: 1–100");}
    private PaginaDTO<Map<String,Object>> reporte(List<?> filas,String[] claves,long total,int page,int size) {
        List<Map<String,Object>> respuesta=new ArrayList<>();
        for(Object registro:filas) {Object[] columnas=(Object[])registro;Map<String,Object> fila=new LinkedHashMap<>();for(int i=0;i<claves.length;i++)fila.put(claves[i],columnas[i]);respuesta.add(fila);}
        return new PaginaDTO<>(respuesta,total,page,size);
    }
}
