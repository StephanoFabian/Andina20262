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
import { Asistencia } from '../../../models/Asistencia';
import { AsistenciaService } from '../../../services/asistencias.service';
import {
  mensajeCampo,
  noVacio,
  rangoFechas,
} from '../../compartido/validadores';
import { mensajeError } from '../../../services/errores';
import { SesionVirtual } from '../../../models/SesionVirtual';
import { SesionVirtualService } from '../../../services/sesiones-virtuales.service';
import { Persona } from '../../../models/Persona';
import { PersonaService } from '../../../services/personas.service';
@Component({
  selector: 'app-asistencias-actualizar',
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
export class AsistenciaActualizarComponent implements OnInit {
  private readonly service = inject(AsistenciaService);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);
  readonly id = input<string>();
  readonly edicion = computed(() => !!this.id());
  readonly guardando = signal(false);
  readonly cargando = signal(false);
  readonly cargaFallida = signal(false);
  readonly mensaje = signal('');
  private readonly sIdSesionVirtual = inject(SesionVirtualService);
  readonly opcionesIdSesionVirtual = signal<SesionVirtual[]>([]);
  readonly busquedaIdSesionVirtual = new FormControl('', { nonNullable: true });
  private readonly sIdPersona = inject(PersonaService);
  readonly opcionesIdPersona = signal<Persona[]>([]);
  readonly busquedaIdPersona = new FormControl('', { nonNullable: true });
  readonly form = inject(FormBuilder).group({
    idSesionVirtual: [null as number | null, [Validators.required]],
    idPersona: [null as number | null, [Validators.required]],
    estado: ['PRESENTE', [Validators.required]],
    observacion: ['', [Validators.maxLength(500)]],
  });
  ngOnInit(): void {
    this.cargarIdSesionVirtual();
    this.cargarIdPersona();

    if (this.edicion()) {
      this.cargando.set(true);
      this.service.listId(Number(this.id())).subscribe({
        next: (dto) => {
          this.form.patchValue(dto);
          if (dto.idSesionVirtual)
            this.sIdSesionVirtual
              .listId(Number(dto.idSesionVirtual))
              .subscribe({
                next: (r) => {
                  if (
                    !this.opcionesIdSesionVirtual().some(
                      (x) => x.idSesionVirtual === r.idSesionVirtual,
                    )
                  )
                    this.opcionesIdSesionVirtual.update((lista) => [
                      r,
                      ...lista,
                    ]);
                },
              });
          if (dto.idPersona)
            this.sIdPersona.listId(Number(dto.idPersona)).subscribe({
              next: (r) => {
                if (
                  !this.opcionesIdPersona().some(
                    (x) => x.idPersona === r.idPersona,
                  )
                )
                  this.opcionesIdPersona.update((lista) => [r, ...lista]);
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
  cargarIdSesionVirtual(): void {
    this.sIdSesionVirtual
      .pagina(0, 20, this.busquedaIdSesionVirtual.value.trim())
      .subscribe({
        next: (p) => this.opcionesIdSesionVirtual.set(p.content),
        error: (err) => this.mensaje.set(mensajeError(err)),
      });
  }
  cargarIdPersona(): void {
    this.sIdPersona
      .pagina(0, 20, this.busquedaIdPersona.value.trim())
      .subscribe({
        next: (p) => this.opcionesIdPersona.set(p.content),
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
    const dto: Asistencia = {
      idAsistencia: this.edicion() ? Number(this.id()) : undefined,
      idSesionVirtual: Number(v.idSesionVirtual),
      idPersona: Number(v.idPersona),
      estado: (v.estado ?? '').trim(),
      observacion: (v.observacion ?? '').trim(),
    };
    this.guardando.set(true);
    this.mensaje.set('');
    const peticion = this.edicion()
      ? this.service.actualizar(dto)
      : this.service.insertar(dto);
    peticion.subscribe({
      next: () => {
        this.snack.open('Registro guardado', 'Cerrar', { duration: 3500 });
        void this.router.navigate(['/asistencias']);
      },
      error: (err) => {
        this.guardando.set(false);
        this.mensaje.set(mensajeError(err));
      },
    });
  }
}
