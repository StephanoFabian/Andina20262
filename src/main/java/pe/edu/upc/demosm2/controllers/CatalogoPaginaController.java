package pe.edu.upc.demosm2.controllers;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.demosm2.entities.*;
import pe.edu.upc.demosm2.dtos.PaginaDTO;
import pe.edu.upc.demosm2.serviceimplements.ConsultaPaginadaService;
import java.util.Map;
@RestController
public class CatalogoPaginaController {
    private final ConsultaPaginadaService servicio;
    public CatalogoPaginaController(ConsultaPaginadaService servicio) { this.servicio=servicio; }
    @GetMapping("/api/colegios/pagina")
    public PaginaDTO<Map<String,Object>> paginaColegio(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(Colegio.class,new String[]{"idColegio=idColegio","nombre=nombre","departamento=departamento","provincia=provincia","distrito=distrito","comunidad=comunidad","tipo_zona=tipo_zona"},new String[]{"nombre","departamento","distrito"},new String[]{"idColegio"},page,size,q);
    }
    @GetMapping("/api/aula/pagina")
    public PaginaDTO<Map<String,Object>> paginaAula(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(Aula.class,new String[]{"idAula=idAula","nombre=nombre","seccion=seccion","capacidad=capacidad","idColegio=colegio.idColegio"},new String[]{"nombre","seccion"},new String[]{"idAula"},page,size,q);
    }
    @GetMapping("/curso/pagina")
    public PaginaDTO<Map<String,Object>> paginaCurso(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(Curso.class,new String[]{"id_curso=id_curso","nombre_curso=nombre_curso","descripcion=descripcion","area=area"},new String[]{"nombre_curso","area"},new String[]{"id_curso"},page,size,q);
    }
    @GetMapping("/api/grados/pagina")
    public PaginaDTO<Map<String,Object>> paginaGrado(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(Grado.class,new String[]{"idGrado=idGrado","nombre=nombre","nivel=nivel"},new String[]{"nombre","nivel"},new String[]{"idGrado"},page,size,q);
    }
    @GetMapping("/api/periodos-academicos/pagina")
    public PaginaDTO<Map<String,Object>> paginaPeriodoAcademico(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(PeriodoAcademico.class,new String[]{"idPeriodo=idPeriodo","nombre=nombre","fechaInicio=fechaInicio","fechaFin=fechaFin","estado=estado"},new String[]{"nombre","estado"},new String[]{"idPeriodo"},page,size,q);
    }
    @GetMapping("/api/matriculas/pagina")
    public PaginaDTO<Map<String,Object>> paginaMatricula(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(Matricula.class,new String[]{"idMatricula=idMatricula","idColegio=colegio.idColegio","nombreColegio=colegio.nombre","idPersona=persona.idPersona"},new String[]{"colegio.nombre","persona.nombresPersona"},new String[]{"idMatricula"},page,size,q);
    }
    @GetMapping("/detalles-matricula/pagina")
    public PaginaDTO<Map<String,Object>> paginaDetalleMatricula(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(DetalleMatricula.class,new String[]{"idDetalleMatricula=idDetalleMatricula","fechaMatricula=fechaMatricula","estado=estado","idMatricula=matricula.idMatricula","idCurso=curso.id_curso","idPeriodo=periodo.idPeriodo","idGrado=grado.idGrado"},new String[]{"estado"},new String[]{"idDetalleMatricula"},page,size,q);
    }
    @GetMapping("/personas/pagina")
    public PaginaDTO<Map<String,Object>> paginaPersona(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(Persona.class,new String[]{"idPersona=idPersona","nombresPersona=nombresPersona","apellidosPersona=apellidosPersona","fechaNacimientoPersona=fechaNacimientoPersona","emailPersona=emailPersona","estadoPersona=estadoPersona","idRol=rol.idTipoPersona"},new String[]{"nombresPersona","apellidosPersona","emailPersona"},new String[]{"idPersona"},page,size,q);
    }
    @GetMapping("/roles/pagina")
    public PaginaDTO<Map<String,Object>> paginaRol(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(Rol.class,new String[]{"idTipoPersona=idTipoPersona","detalle=detalle"},new String[]{"detalle"},new String[]{"idTipoPersona"},page,size,q);
    }
    @GetMapping("/perfiles-academicos/pagina")
    public PaginaDTO<Map<String,Object>> paginaPerfilAcademico(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(PerfilAcademico.class,new String[]{"idPerfilAcademico=idPerfilAcademico","detallePA=detallePA","notasPA=notasPA"},new String[]{"detallePA"},new String[]{"idPerfilAcademico"},page,size,q);
    }
    @GetMapping("/materiales/pagina")
    public PaginaDTO<Map<String,Object>> paginaMaterial(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(Material.class,new String[]{"idMaterial=idMaterial","titulo=titulo","descripcion=descripcion","tipo=tipo","urlArchivo=urlArchivo","fechaPublicacion=fechaPublicacion","idPersona=persona.idPersona","nombreAutor=persona.nombresPersona","apellidosAutor=persona.apellidosPersona"},new String[]{"titulo","tipo","persona.nombresPersona","persona.apellidosPersona"},new String[]{"idMaterial"},page,size,q);
    }
    @GetMapping("/materiales-cursos/pagina")
    public PaginaDTO<Map<String,Object>> paginaMaterialCurso(@RequestParam(required=false) Long idMaterial,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(MaterialCurso.class,new String[]{"idMaterial=id.idMaterial","idCurso=id.idCurso","material=material.titulo","curso=curso.nombre_curso"},new String[]{"material.titulo","curso.nombre_curso"},new String[]{"id.idMaterial","id.idCurso"},page,size,q,idMaterial==null ? Map.of() : Map.of("id.idMaterial",idMaterial));
    }
    @GetMapping("/asignacion/pagina")
    public PaginaDTO<Map<String,Object>> paginaAsignacionDocente(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(AsignacionDocente.class,new String[]{"id_asignacion=id_asignacion","id_aula=id_aula","id_curso=curso.id_curso","id_periodo=periodoAcademico.idPeriodo","id_persona=persona.idPersona","id_colegio=colegio.idColegio","modalidad=modalidad","horassemanales=horassemanales"},new String[]{"id_aula","modalidad"},new String[]{"id_asignacion"},page,size,q);
    }
    @GetMapping("/api/sesiones-virtuales/pagina")
    public PaginaDTO<Map<String,Object>> paginaSesionVirtual(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(SesionVirtual.class,new String[]{"idSesionVirtual=idSesionVirtual","titulo=titulo","fecha=fecha","horaInicio=horaInicio","horaFin=horaFin","enlace=enlace","estado=estado","idAsignacion=asignacion.id_asignacion"},new String[]{"titulo","estado"},new String[]{"idSesionVirtual"},page,size,q);
    }
    @GetMapping("/api/asistencias/pagina")
    public PaginaDTO<Map<String,Object>> paginaAsistencia(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(Asistencia.class,new String[]{"idAsistencia=idAsistencia","estado=estado","observacion=observacion","idSesionVirtual=sesionVirtual.idSesionVirtual","idPersona=persona.idPersona"},new String[]{"estado","persona.nombresPersona"},new String[]{"idAsistencia"},page,size,q);
    }
    @GetMapping("/api/horarios/pagina")
    public PaginaDTO<Map<String,Object>> paginaHorario(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(Horario.class,new String[]{"idHorario=idHorario","diaSemana=diaSemana","horaInicio=horaInicio","horaFin=horaFin","idAsignacion=asignacion.id_asignacion"},new String[]{"asignacion.modalidad"},new String[]{"idHorario"},page,size,q);
    }
    @GetMapping("/api/convenios/pagina")
    public PaginaDTO<Map<String,Object>> paginaConvenio(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(Convenio.class,new String[]{"idConvenio=idConvenio","nombre=nombre","institucion=institucion","entidad=institucion","tipoEntidad=tipoEntidad","region=region","presupuesto=presupuesto","fechaInicio=fechaInicio","fechaFin=fechaFin","estado=estado","idColegio=colegio.idColegio"},new String[]{"nombre","institucion","region","estado"},new String[]{"idConvenio"},page,size,q);
    }
    @GetMapping("/api/incidencias/pagina")
    public PaginaDTO<Map<String,Object>> paginaIncidencia(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(defaultValue="") String q) {
        return servicio.pagina(Incidencia.class,new String[]{"idIncidencia=idIncidencia","titulo=titulo","descripcion=descripcion","fecha=fecha","estado=estado","prioridad=prioridad","idPersona=persona.idPersona","idColegio=colegio.idColegio"},new String[]{"titulo","estado","prioridad"},new String[]{"idIncidencia"},page,size,q);
    }
}
