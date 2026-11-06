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
import { SesionVirtual } from '../../../models/SesionVirtual';
import { SesionVirtualService } from '../../../services/sesiones-virtuales.service';
import {
  mensajeCampo,
  noVacio,
  rangoFechas,
} from '../../compartido/validadores';
import { mensajeError } from '../../../services/errores';
import { AsignacionDocente } from '../../../models/AsignacionDocente';
import { AsignacionDocenteService } from '../../../services/asignaciones.service';
@Component({
  selector: 'app-sesiones-virtuales-insertar',
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
export class SesionVirtualInsertarComponent implements OnInit {
  private readonly service = inject(SesionVirtualService);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);
  readonly id = input<string>();
  readonly edicion = computed(() => !!this.id());
  readonly guardando = signal(false);
  readonly cargando = signal(false);
  readonly cargaFallida = signal(false);
  readonly mensaje = signal('');
  private readonly sIdAsignacion = inject(AsignacionDocenteService);
  readonly opcionesIdAsignacion = signal<AsignacionDocente[]>([]);
  readonly busquedaIdAsignacion = new FormControl('', { nonNullable: true });
  readonly form = inject(FormBuilder).group({
    titulo: ['', [Validators.required, noVacio, Validators.maxLength(150)]],
    idAsignacion: [null as number | null, [Validators.required]],
    fecha: ['', [Validators.required]],
    horaInicio: ['', [Validators.required]],
    horaFin: ['', [Validators.required]],
    enlace: [
      '',
      [
        Validators.required,
        Validators.maxLength(500),
        Validators.pattern(/^https?:\/\/\S+$/),
      ],
    ],
    estado: ['PROGRAMADA', [Validators.required]],
  });
  ngOnInit(): void {
    this.cargarIdAsignacion();

    if (this.edicion()) {
      this.cargando.set(true);
      this.service.listId(Number(this.id())).subscribe({
        next: (dto) => {
          this.form.patchValue(dto);
          if (dto.idAsignacion)
            this.sIdAsignacion.listId(Number(dto.idAsignacion)).subscribe({
              next: (r) => {
                if (
                  !this.opcionesIdAsignacion().some(
                    (x) => x.id_asignacion === r.id_asignacion,
                  )
                )
                  this.opcionesIdAsignacion.update((lista) => [r, ...lista]);
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
  cargarIdAsignacion(): void {
    this.sIdAsignacion
      .pagina(0, 20, this.busquedaIdAsignacion.value.trim())
      .subscribe({
        next: (p) => this.opcionesIdAsignacion.set(p.content),
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
    const dto: SesionVirtual = {
      idSesionVirtual: this.edicion() ? Number(this.id()) : undefined,
      titulo: (v.titulo ?? '').trim(),
      idAsignacion: Number(v.idAsignacion),
      fecha: (v.fecha ?? '').trim(),
      horaInicio: (v.horaInicio ?? '').trim(),
      horaFin: (v.horaFin ?? '').trim(),
      enlace: (v.enlace ?? '').trim(),
      estado: (v.estado ?? '').trim(),
    };
    this.guardando.set(true);
    this.mensaje.set('');
    const peticion = this.edicion()
      ? this.service.actualizar(dto)
      : this.service.insertar(dto);
    peticion.subscribe({
      next: () => {
        this.snack.open('Registro guardado', 'Cerrar', { duration: 3500 });
        void this.router.navigate(['/sesiones-virtuales']);
      },
      error: (err) => {
        this.guardando.set(false);
        this.mensaje.set(mensajeError(err));
      },
    });
  }
}
