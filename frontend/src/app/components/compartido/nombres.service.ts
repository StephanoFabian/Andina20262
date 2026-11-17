import { Injectable, inject, signal } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Pagina } from '../../models/modulo';
import { Persona } from '../../models/Persona';
import { Colegio } from '../../models/Colegio';
import { Curso } from '../../models/Curso';
import { Aula } from '../../models/Aula';
import { Grado } from '../../models/Grado';
import { PeriodoAcademico } from '../../models/PeriodoAcademico';
import { AsignacionDocente } from '../../models/AsignacionDocente';
import { SesionVirtual } from '../../models/SesionVirtual';
import { Matricula } from '../../models/Matricula';
import { Rol } from '../../models/Rol';
import { PersonaService } from '../../services/personas.service';
import { ColegioService } from '../../services/colegios.service';
import { CursoService } from '../../services/cursos.service';
import { AulaService } from '../../services/aulas.service';
import { GradoService } from '../../services/grados.service';
import { PeriodoAcademicoService } from '../../services/periodos-academicos.service';
import { AsignacionDocenteService } from '../../services/asignaciones.service';
import { SesionVirtualService } from '../../services/sesiones-virtuales.service';
import { MatriculaService } from '../../services/matriculas.service';
import { RolService } from '../../services/roles.service';

export type TipoNombre =
  | 'persona'
  | 'colegio'
  | 'curso'
  | 'aula'
  | 'grado'
  | 'periodo'
  | 'asignacion'
  | 'sesion'
  | 'matricula'
  | 'rol';

export interface NombreResuelto {
  texto: string;
  /** ok: nombre encontrado · cargando: aún se consulta · falta: no se pudo obtener (se muestra el número) */
  estado: 'ok' | 'cargando' | 'falta';
}

interface Fuente {
  pagina: () => Observable<Pagina<unknown>>;
  uno: (id: number) => Observable<unknown>;
  id: (registro: any) => number | undefined;
}

/** Tamaño máximo que admite /pagina en el backend */
const LOTE = 100;

const TIPOS: TipoNombre[] = [
  'persona',
  'colegio',
  'curso',
  'aula',
  'grado',
  'periodo',
  'asignacion',
  'sesion',
  'matricula',
  'rol',
];

/**
 * Convierte los IDs de los listados en nombres, solo en el frontend.
 *
 * Los DTO de listado del backend traen el ID de la persona, colegio, curso, etc.,
 * pero no su nombre. Para no pedir un registro por cada fila:
 *  1. La primera vez que se necesita un tipo se pide UNA página de 100 registros.
 *  2. Solo los IDs que no vinieron en esa página se piden uno por uno, y una sola vez.
 *  3. Todo queda en memoria y lo comparten todos los listados.
 * La caché se vacía al entrar a un formulario, porque ahí es donde cambian los nombres.
 */
@Injectable({ providedIn: 'root' })
export class NombresService {
  private readonly personas = inject(PersonaService);
  private readonly colegios = inject(ColegioService);
  private readonly cursos = inject(CursoService);
  private readonly aulas = inject(AulaService);
  private readonly grados = inject(GradoService);
  private readonly periodos = inject(PeriodoAcademicoService);
  private readonly asignaciones = inject(AsignacionDocenteService);
  private readonly sesiones = inject(SesionVirtualService);
  private readonly matriculas = inject(MatriculaService);
  private readonly roles = inject(RolService);

  private readonly fuentes: Record<TipoNombre, Fuente> = {
    persona: {
      pagina: () => this.personas.pagina(0, LOTE),
      uno: (id) => this.personas.listId(id),
      id: (r: Persona) => r.idPersona,
    },
    colegio: {
      pagina: () => this.colegios.pagina(0, LOTE),
      uno: (id) => this.colegios.listId(id),
      id: (r: Colegio) => r.idColegio,
    },
    curso: {
      pagina: () => this.cursos.pagina(0, LOTE),
      uno: (id) => this.cursos.listId(id),
      id: (r: Curso) => r.id_curso,
    },
    aula: {
      pagina: () => this.aulas.pagina(0, LOTE),
      uno: (id) => this.aulas.listId(id),
      id: (r: Aula) => r.idAula,
    },
    grado: {
      pagina: () => this.grados.pagina(0, LOTE),
      uno: (id) => this.grados.listId(id),
      id: (r: Grado) => r.idGrado,
    },
    periodo: {
      pagina: () => this.periodos.pagina(0, LOTE),
      uno: (id) => this.periodos.listId(id),
      id: (r: PeriodoAcademico) => r.idPeriodo,
    },
    asignacion: {
      pagina: () => this.asignaciones.pagina(0, LOTE),
      uno: (id) => this.asignaciones.listId(id),
      id: (r: AsignacionDocente) => r.id_asignacion,
    },
    sesion: {
      pagina: () => this.sesiones.pagina(0, LOTE),
      uno: (id) => this.sesiones.listId(id),
      id: (r: SesionVirtual) => r.idSesionVirtual,
    },
    matricula: {
      pagina: () => this.matriculas.pagina(0, LOTE),
      uno: (id) => this.matriculas.listId(id),
      id: (r: Matricula) => r.idMatricula,
    },
    rol: {
      pagina: () => this.roles.pagina(0, LOTE),
      uno: (id) => this.roles.listId(id),
      id: (r: Rol) => r.idTipoPersona,
    },
  };

  /** Registros por tipo e ID. null = se consultó y no se pudo obtener. */
  private readonly datos = signal(this.vacio<Record<number, unknown>>({}));
  private precarga = this.vacio<'no' | 'cargando' | 'lista'>('no');
  private cola = this.vacio<Set<number>>(null, () => new Set<number>());
  private enVuelo = new Set<string>();
  private programado = false;
  /** Cambia en cada vaciado: descarta respuestas que llegan de la caché anterior */
  private generacion = 0;

  constructor() {
    inject(Router).events.subscribe((e) => {
      if (
        e instanceof NavigationEnd &&
        /\/(insertar|editar|nuevo|ediciones|crear-con-ia)(\/|$|\?)/.test(
          e.urlAfterRedirects,
        )
      )
        this.vaciar();
    });
  }

  /** Nombre para mostrar. Es reactivo: usar dentro de plantillas o computed(). */
  nombre(tipo: TipoNombre, id: unknown): NombreResuelto {
    const n = Number(id);
    if (id === null || id === undefined || id === '' || !Number.isFinite(n))
      return { texto: '—', estado: 'ok' };
    const registro = this.registro(tipo, n);
    if (registro === undefined) return { texto: '…', estado: 'cargando' };
    if (registro === null) return { texto: `#${n}`, estado: 'falta' };
    return this.describir(tipo, registro, n);
  }

  /** Registro en caché; si no está, lo deja pedido. undefined = todavía cargando. */
  registro<T = unknown>(tipo: TipoNombre, id: number): T | null | undefined {
    const valor = this.datos()[tipo][id];
    if (valor === undefined) this.solicitar(tipo, id);
    return valor as T | null | undefined;
  }

  vaciar(): void {
    this.generacion++;
    this.precarga = this.vacio<'no' | 'cargando' | 'lista'>('no');
    this.cola = this.vacio<Set<number>>(null, () => new Set<number>());
    this.enVuelo = new Set<string>();
    this.datos.set(this.vacio<Record<number, unknown>>({}));
  }

  private describir(
    tipo: TipoNombre,
    registro: unknown,
    id: number,
  ): NombreResuelto {
    const ok = (texto: string): NombreResuelto =>
      texto.trim()
        ? { texto: texto.trim(), estado: 'ok' }
        : { texto: `#${id}`, estado: 'falta' };
    const unir = (...partes: (string | null | undefined)[]) =>
      partes.filter((p) => !!p && p.trim() !== '').join(' ');
    switch (tipo) {
      case 'persona': {
        const p = registro as Persona;
        return ok(unir(p.nombresPersona, p.apellidosPersona));
      }
      case 'colegio':
        return ok((registro as Colegio).nombre ?? '');
      case 'curso':
        return ok((registro as Curso).nombre_curso ?? '');
      case 'aula': {
        const a = registro as Aula;
        return ok(unir(a.nombre, a.seccion));
      }
      case 'grado': {
        const g = registro as Grado;
        return ok([g.nombre, g.nivel].filter(Boolean).join(' · '));
      }
      case 'periodo':
        return ok((registro as PeriodoAcademico).nombre ?? '');
      case 'sesion':
        return ok((registro as SesionVirtual).titulo ?? '');
      case 'rol':
        return ok((registro as Rol).detalle ?? '');
      case 'asignacion': {
        // Una asignación no tiene nombre propio: se describe con su curso y su docente
        const a = registro as AsignacionDocente;
        return this.componer(
          this.nombre('curso', a.id_curso),
          this.nombre('persona', a.id_persona),
          id,
        );
      }
      case 'matricula': {
        // Una matrícula se describe con el estudiante y su colegio
        const m = registro as Matricula;
        const colegio: NombreResuelto = m.nombreColegio
          ? { texto: m.nombreColegio, estado: 'ok' }
          : this.nombre('colegio', m.idColegio);
        return this.componer(this.nombre('persona', m.idPersona), colegio, id);
      }
    }
  }

  private componer(
    a: NombreResuelto,
    b: NombreResuelto,
    id: number,
  ): NombreResuelto {
    if (a.estado === 'cargando' || b.estado === 'cargando')
      return { texto: '…', estado: 'cargando' };
    const partes = [a, b].filter((x) => x.estado === 'ok' && x.texto !== '—');
    return partes.length
      ? { texto: partes.map((x) => x.texto).join(' · '), estado: 'ok' }
      : { texto: `#${id}`, estado: 'falta' };
  }

  private solicitar(tipo: TipoNombre, id: number): void {
    if (this.enVuelo.has(`${tipo}:${id}`)) return;
    this.cola[tipo].add(id);
    if (this.programado) return;
    this.programado = true;
    // Se agrupa: todas las filas de una tabla piden sus nombres en el mismo ciclo
    queueMicrotask(() => {
      this.programado = false;
      for (const t of TIPOS) this.despachar(t);
    });
  }

  private despachar(tipo: TipoNombre): void {
    if (!this.cola[tipo].size || this.precarga[tipo] === 'cargando') return;
    const generacion = this.generacion;
    if (this.precarga[tipo] === 'no') {
      this.precarga[tipo] = 'cargando';
      const terminar = (registros: unknown[]) => {
        if (generacion !== this.generacion) return;
        this.precarga[tipo] = 'lista';
        this.guardar(tipo, registros);
        this.despachar(tipo);
      };
      this.fuentes[tipo].pagina().subscribe({
        next: (p) => terminar(p?.content ?? []),
        error: () => terminar([]),
      });
      return;
    }
    const conocidos = this.datos()[tipo];
    const faltantes = [...this.cola[tipo]].filter(
      (id) => conocidos[id] === undefined,
    );
    this.cola[tipo].clear();
    for (const id of faltantes) {
      const llave = `${tipo}:${id}`;
      if (this.enVuelo.has(llave)) continue;
      this.enVuelo.add(llave);
      const terminar = (registro: unknown) => {
        if (generacion !== this.generacion) return;
        this.enVuelo.delete(llave);
        this.datos.update((d) => ({
          ...d,
          [tipo]: { ...d[tipo], [id]: registro ?? null },
        }));
      };
      this.fuentes[tipo].uno(id).subscribe({
        next: (r) => terminar(r),
        error: () => terminar(null),
      });
    }
  }

  private guardar(tipo: TipoNombre, registros: unknown[]): void {
    const nuevos: Record<number, unknown> = {};
    for (const r of registros) {
      const id = this.fuentes[tipo].id(r);
      if (id !== undefined && id !== null) nuevos[Number(id)] = r;
    }
    this.datos.update((d) => ({ ...d, [tipo]: { ...d[tipo], ...nuevos } }));
  }

  private vacio<T>(valor: T | null, crear?: () => T): Record<TipoNombre, T> {
    const r = {} as Record<TipoNombre, T>;
    for (const t of TIPOS) r[t] = crear ? crear() : (valor as T);
    return r;
  }
}
