import {
  Component,
  OnInit,
  computed,
  inject,
  input,
  signal,
} from '@angular/core';
import {
  FormBuilder,
  FormControl,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AsignacionDocente } from '../../../models/AsignacionDocente';
import { AsignacionDocenteService } from '../../../services/asignaciones.service';
import {
  mensajeCampo,
  noVacio,
  rangoFechas,
} from '../../compartido/validadores';
import { mensajeError } from '../../../services/errores';
import { Curso } from '../../../models/Curso';
import { CursoService } from '../../../services/cursos.service';
import { PeriodoAcademico } from '../../../models/PeriodoAcademico';
import { PeriodoAcademicoService } from '../../../services/periodos-academicos.service';
import { Persona } from '../../../models/Persona';
import { PersonaService } from '../../../services/personas.service';
import { Colegio } from '../../../models/Colegio';
import { ColegioService } from '../../../services/colegios.service';
@Component({
  selector: 'app-asignaciones-insertar',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
  ],
  templateUrl: './insertarcomponent.html',
})
export class AsignacionDocenteInsertarComponent implements OnInit {
  private readonly service = inject(AsignacionDocenteService);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);
  readonly id = input<string>();
  readonly edicion = computed(() => !!this.id());
  readonly guardando = signal(false);
  readonly cargando = signal(false);
  readonly cargaFallida = signal(false);
  readonly mensaje = signal('');
  private readonly sId_curso = inject(CursoService);
  readonly opcionesId_curso = signal<Curso[]>([]);
  readonly busquedaId_curso = new FormControl('', { nonNullable: true });
  private readonly sId_periodo = inject(PeriodoAcademicoService);
  readonly opcionesId_periodo = signal<PeriodoAcademico[]>([]);
  readonly busquedaId_periodo = new FormControl('', { nonNullable: true });
  private readonly sId_persona = inject(PersonaService);
  readonly opcionesId_persona = signal<Persona[]>([]);
  readonly busquedaId_persona = new FormControl('', { nonNullable: true });
  private readonly sId_colegio = inject(ColegioService);
  readonly opcionesId_colegio = signal<Colegio[]>([]);
  readonly busquedaId_colegio = new FormControl('', { nonNullable: true });
  readonly form = inject(FormBuilder).group({
    id_aula: ['', [Validators.required, noVacio, Validators.maxLength(200)]],
    id_curso: [null as number | null, [Validators.required]],
    id_periodo: [null as number | null, [Validators.required]],
    id_persona: [null as number | null, [Validators.required]],
    id_colegio: [null as number | null, [Validators.required]],
    modalidad: ['PRESENCIAL', [Validators.required]],
    horassemanales: [
      null as number | null,
      [Validators.required, Validators.min(1), Validators.max(168)],
    ],
  });
  ngOnInit(): void {
    this.cargarId_curso();
    this.cargarId_periodo();
    this.cargarId_persona();
    this.cargarId_colegio();

    if (this.edicion()) {
      this.cargando.set(true);
      this.service.listId(Number(this.id())).subscribe({
        next: (dto) => {
          this.form.patchValue(dto);
          if (dto.id_curso)
            this.sId_curso.listId(Number(dto.id_curso)).subscribe({
              next: (r) => {
                if (
                  !this.opcionesId_curso().some(
                    (x) => x.id_curso === r.id_curso,
                  )
                )
                  this.opcionesId_curso.update((lista) => [r, ...lista]);
              },
            });
          if (dto.id_periodo)
            this.sId_periodo.listId(Number(dto.id_periodo)).subscribe({
              next: (r) => {
                if (
                  !this.opcionesId_periodo().some(
                    (x) => x.idPeriodo === r.idPeriodo,
                  )
                )
                  this.opcionesId_periodo.update((lista) => [r, ...lista]);
              },
            });
          if (dto.id_persona)
            this.sId_persona.listId(Number(dto.id_persona)).subscribe({
              next: (r) => {
                if (
                  !this.opcionesId_persona().some(
                    (x) => x.idPersona === r.idPersona,
                  )
                )
                  this.opcionesId_persona.update((lista) => [r, ...lista]);
              },
            });
          if (dto.id_colegio)
            this.sId_colegio.listId(Number(dto.id_colegio)).subscribe({
              next: (r) => {
                if (
                  !this.opcionesId_colegio().some(
                    (x) => x.idColegio === r.idColegio,
                  )
                )
                  this.opcionesId_colegio.update((lista) => [r, ...lista]);
              },
            });
          this.cargando.set(false);
        },
        error: (err) => {
          this.cargando.set(false);
          this.cargaFallida.set(true);
          this.mensaje.set(mensajeError(err));
        },
      });
    }
  }
  cargarId_curso(): void {
    this.sId_curso
      .pagina(0, 20, this.busquedaId_curso.value.trim())
      .subscribe({
        next: (p) => this.opcionesId_curso.set(p.content),
        error: (err) => this.mensaje.set(mensajeError(err)),
      });
  }
  cargarId_periodo(): void {
    this.sId_periodo
      .pagina(0, 20, this.busquedaId_periodo.value.trim())
      .subscribe({
        next: (p) => this.opcionesId_periodo.set(p.content),
        error: (err) => this.mensaje.set(mensajeError(err)),
      });
  }
  cargarId_persona(): void {
    this.sId_persona
      .pagina(0, 20, this.busquedaId_persona.value.trim())
      .subscribe({
        next: (p) => this.opcionesId_persona.set(p.content),
        error: (err) => this.mensaje.set(mensajeError(err)),
      });
  }
  cargarId_colegio(): void {
    this.sId_colegio
      .pagina(0, 20, this.busquedaId_colegio.value.trim())
      .subscribe({
        next: (p) => this.opcionesId_colegio.set(p.content),
        error: (err) => this.mensaje.set(mensajeError(err)),
      });
  }
  error(campo: string): string {
    return mensajeCampo(this.form.get(campo)!);
  }
  guardar(): void {
    if (this.guardando() || this.cargando() || this.cargaFallida()) return;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const dto: AsignacionDocente = {
      id_asignacion: this.edicion() ? Number(this.id()) : undefined,
      id_aula: (v.id_aula ?? '').trim(),
      id_curso: Number(v.id_curso),
      id_periodo: Number(v.id_periodo),
      id_persona: Number(v.id_persona),
      id_colegio: Number(v.id_colegio),
      modalidad: (v.modalidad ?? '').trim(),
      horassemanales: Number(v.horassemanales),
    };
    this.guardando.set(true);
    this.mensaje.set('');
    const peticion = this.edicion()
      ? this.service.actualizar(dto)
      : this.service.insertar(dto);
    peticion.subscribe({
      next: () => {
        this.snack.open('Registro guardado', 'Cerrar', { duration: 3500 });
        void this.router.navigate(['/asignaciones']);
      },
      error: (err) => {
        this.guardando.set(false);
        this.mensaje.set(mensajeError(err));
      },
    });
  }
}
