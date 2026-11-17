import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { forkJoin } from 'rxjs';
import { MaterialDTO } from '../../../models/material';
import { mensajeError } from '../../../services/errores';
import { Materialcursoservice } from '../../../services/materialcursoservice';
import { Materialservice } from '../../../services/materialservice';
import { CursoChip } from '../../compartido/curso-chip/curso-chip';
import { Encabezado } from '../../compartido/encabezado/encabezado';
import { EstadoVacio } from '../../compartido/estado-vacio/estado-vacio';
import { FechaPePipe } from '../../compartido/formato';
import { colorCurso, tipoMaterial } from '../../compartido/iconos';

interface Tarjeta extends MaterialDTO {
  icono: string;
  tipoTexto: string;
}

interface Grupo {
  curso: string;
  color: number;
  materiales: Tarjeta[];
}

/** Máximo que admite /pagina en el backend */
const LOTE = 100;
const SIN_CURSO = 'Otros materiales';

/**
 * Vista de materiales para el estudiante: tarjetas agrupadas por curso.
 * La usan "Materiales" y "Materiales por curso" cuando el rol es ESTUDIANTE.
 * Combina dos endpoints que ya existen: GET /materiales/pagina (título, tipo y
 * archivo) y GET /materiales-cursos/pagina (a qué curso pertenece cada uno).
 */
@Component({
  selector: 'app-materiales-estudiante',
  imports: [
    Encabezado,
    EstadoVacio,
    CursoChip,
    FechaPePipe,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressBarModule,
  ],
  templateUrl: './material-estudiante.html',
})
export class MaterialesEstudiante implements OnInit {
  private readonly materiales = inject(Materialservice);
  private readonly materialesCursos = inject(Materialcursoservice);

  /** "materiales" muestra también los que no tienen curso; "materiales-cursos", solo los asociados */
  readonly modulo = input.required<'materiales' | 'materiales-cursos'>();

  protected readonly cargando = signal(true);
  protected readonly error = signal('');
  protected readonly filtro = signal('');
  /** Hay más registros de los que caben en una consulta */
  protected readonly incompleto = signal(false);
  private readonly grupos = signal<Grupo[]>([]);

  /** Grupos filtrados por el texto de búsqueda (título o tipo) */
  protected readonly visibles = computed(() => {
    const texto = this.filtro().trim().toLowerCase();
    const grupos = this.grupos().filter(
      (g) => this.modulo() === 'materiales' || g.curso !== SIN_CURSO,
    );
    if (!texto) return grupos;
    return grupos
      .map((g) => ({
        ...g,
        materiales: g.materiales.filter(
          (m) =>
            m.titulo.toLowerCase().includes(texto) ||
            m.tipoTexto.toLowerCase().includes(texto) ||
            g.curso.toLowerCase().includes(texto),
        ),
      }))
      .filter((g) => g.materiales.length > 0);
  });
  protected readonly total = computed(
    () =>
      new Set(
        this.visibles().flatMap((g) => g.materiales.map((m) => m.idMaterial)),
      ).size,
  );

  ngOnInit(): void {
    forkJoin({
      materiales: this.materiales.pagina(0, LOTE),
      cursos: this.materialesCursos.pagina(0, LOTE),
    }).subscribe({
      next: ({ materiales, cursos }) => {
        this.incompleto.set(
          materiales.totalElements > materiales.content.length ||
            cursos.totalElements > cursos.content.length,
        );
        this.grupos.set(this.agrupar(materiales.content, cursos.content));
        this.cargando.set(false);
      },
      error: (err: unknown) => {
        this.error.set(mensajeError(err));
        this.cargando.set(false);
      },
    });
  }

  private agrupar(
    materiales: MaterialDTO[],
    asociaciones: { idMaterial: number; curso?: string }[],
  ): Grupo[] {
    const porId = new Map<number, Tarjeta>();
    for (const m of materiales) {
      if (m.idMaterial === undefined) continue;
      const tipo = tipoMaterial(m.tipo);
      porId.set(m.idMaterial, { ...m, icono: tipo.icono, tipoTexto: tipo.texto });
    }
    const grupos = new Map<string, Tarjeta[]>();
    const conCurso = new Set<number>();
    for (const a of asociaciones) {
      const material = porId.get(a.idMaterial);
      const curso = a.curso?.trim();
      if (!material || !curso) continue;
      conCurso.add(a.idMaterial);
      grupos.set(curso, [...(grupos.get(curso) ?? []), material]);
    }
    const resultado: Grupo[] = [...grupos.entries()]
      .sort(([a], [b]) => a.localeCompare(b, 'es'))
      .map(([curso, lista]) => ({
        curso,
        color: colorCurso(curso),
        materiales: lista,
      }));
    const sueltos = [...porId.values()].filter(
      (m) => !conCurso.has(m.idMaterial!),
    );
    if (sueltos.length)
      resultado.push({ curso: SIN_CURSO, color: -1, materiales: sueltos });
    return resultado;
  }
}
