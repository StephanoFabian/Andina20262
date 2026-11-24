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
import { DetalleMatricula } from '../../../models/DetalleMatricula';
import { DetalleMatriculaService } from '../../../services/detalles-matricula.service';
import {
  mensajeCampo,
  noVacio,
  rangoFechas,
} from '../../compartido/validadores';
import { mensajeError } from '../../../services/errores';
import { Matricula } from '../../../models/Matricula';
import { MatriculaService } from '../../../services/matriculas.service';
import { Curso } from '../../../models/Curso';
import { CursoService } from '../../../services/cursos.service';
import { PeriodoAcademico } from '../../../models/PeriodoAcademico';
import { PeriodoAcademicoService } from '../../../services/periodos-academicos.service';
import { Grado } from '../../../models/Grado';
import { GradoService } from '../../../services/grados.service';
@Component({
  selector: 'app-detalles-matricula-actualizar',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
  ],
  templateUrl: './actualizarcomponent.html',
})
export class DetalleMatriculaActualizarComponent implements OnInit {
  private readonly service = inject(DetalleMatriculaService);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);
  readonly id = input<string>();
  readonly edicion = computed(() => !!this.id());
  readonly guardando = signal(false);
  readonly cargando = signal(false);
  readonly cargaFallida = signal(false);
  readonly mensaje = signal('');
  private readonly sIdMatricula = inject(MatriculaService);
  readonly opcionesIdMatricula = signal<Matricula[]>([]);
  readonly busquedaIdMatricula = new FormControl('', { nonNullable: true });
  private readonly sIdCurso = inject(CursoService);
  readonly opcionesIdCurso = signal<Curso[]>([]);
  readonly busquedaIdCurso = new FormControl('', { nonNullable: true });
  private readonly sIdPeriodo = inject(PeriodoAcademicoService);
  readonly opcionesIdPeriodo = signal<PeriodoAcademico[]>([]);
  readonly busquedaIdPeriodo = new FormControl('', { nonNullable: true });
  private readonly sIdGrado = inject(GradoService);
  readonly opcionesIdGrado = signal<Grado[]>([]);
  readonly busquedaIdGrado = new FormControl('', { nonNullable: true });
  readonly form = inject(FormBuilder).group({
    fechaMatricula: ['', [Validators.required]],
    estado: ['VIGENTE', [Validators.required]],
    idMatricula: [null as number | null, [Validators.required]],
    idCurso: [null as number | null, [Validators.required]],
    idPeriodo: [null as number | null, [Validators.required]],
    idGrado: [null as number | null, [Validators.required]],
  });
  ngOnInit(): void {
    this.cargarIdMatricula();
    this.cargarIdCurso();
    this.cargarIdPeriodo();
    this.cargarIdGrado();

    if (this.edicion()) {
      this.cargando.set(true);
      this.service.listId(Number(this.id())).subscribe({
        next: (dto) => {
          this.form.patchValue(dto);
          if (dto.idMatricula)
            this.sIdMatricula.listId(Number(dto.idMatricula)).subscribe({
              next: (r) => {
                if (
                  !this.opcionesIdMatricula().some(
                    (x) => x.idMatricula === r.idMatricula,
                  )
                )
                  this.opcionesIdMatricula.update((lista) => [r, ...lista]);
              },
            });
          if (dto.idCurso)
            this.sIdCurso.listId(Number(dto.idCurso)).subscribe({
              next: (r) => {
                if (
                  !this.opcionesIdCurso().some((x) => x.id_curso === r.id_curso)
                )
                  this.opcionesIdCurso.update((lista) => [r, ...lista]);
              },
            });
          if (dto.idPeriodo)
            this.sIdPeriodo.listId(Number(dto.idPeriodo)).subscribe({
              next: (r) => {
                if (
                  !this.opcionesIdPeriodo().some(
                    (x) => x.idPeriodo === r.idPeriodo,
                  )
                )
                  this.opcionesIdPeriodo.update((lista) => [r, ...lista]);
              },
            });
          if (dto.idGrado)
            this.sIdGrado.listId(Number(dto.idGrado)).subscribe({
              next: (r) => {
                if (
                  !this.opcionesIdGrado().some((x) => x.idGrado === r.idGrado)
                )
                  this.opcionesIdGrado.update((lista) => [r, ...lista]);
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
  cargarIdMatricula(): void {
    this.sIdMatricula
      .pagina(0, 20, this.busquedaIdMatricula.value.trim())
      .subscribe({
        next: (p) => this.opcionesIdMatricula.set(p.content),
        error: (err) => this.mensaje.set(mensajeError(err)),
      });
  }
  cargarIdCurso(): void {
    this.sIdCurso
      .pagina(0, 20, this.busquedaIdCurso.value.trim())
      .subscribe({
        next: (p) => this.opcionesIdCurso.set(p.content),
        error: (err) => this.mensaje.set(mensajeError(err)),
      });
  }
  cargarIdPeriodo(): void {
    this.sIdPeriodo
      .pagina(0, 20, this.busquedaIdPeriodo.value.trim())
      .subscribe({
        next: (p) => this.opcionesIdPeriodo.set(p.content),
        error: (err) => this.mensaje.set(mensajeError(err)),
      });
  }
  cargarIdGrado(): void {
    this.sIdGrado
      .pagina(0, 20, this.busquedaIdGrado.value.trim())
      .subscribe({
        next: (p) => this.opcionesIdGrado.set(p.content),
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
    const dto: DetalleMatricula = {
      idDetalleMatricula: this.edicion() ? Number(this.id()) : undefined,
      fechaMatricula: (v.fechaMatricula ?? '').trim(),
      estado: (v.estado ?? '').trim(),
      idMatricula: Number(v.idMatricula),
      idCurso: Number(v.idCurso),
      idPeriodo: Number(v.idPeriodo),
      idGrado: Number(v.idGrado),
    };
    this.guardando.set(true);
    this.mensaje.set('');
    const peticion = this.edicion()
      ? this.service.actualizar(dto)
      : this.service.insertar(dto);
    peticion.subscribe({
      next: () => {
        this.snack.open('Registro guardado', 'Cerrar', { duration: 3500 });
        void this.router.navigate(['/detalles-matricula']);
      },
      error: (err) => {
        this.guardando.set(false);
        this.mensaje.set(mensajeError(err));
      },
    });
  }
}
