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
import { Incidencia } from '../../../models/Incidencia';
import { IncidenciaService } from '../../../services/incidencias.service';
import {
  mensajeCampo,
  noVacio,
  rangoFechas,
} from '../../compartido/validadores';
import { mensajeError } from '../../../services/errores';
import { Persona } from '../../../models/Persona';
import { PersonaService } from '../../../services/personas.service';
import { Colegio } from '../../../models/Colegio';
import { ColegioService } from '../../../services/colegios.service';
@Component({
  selector: 'app-incidencias-actualizar',
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
export class IncidenciaActualizarComponent implements OnInit {
  private readonly service = inject(IncidenciaService);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);
  readonly id = input<string>();
  readonly edicion = computed(() => !!this.id());
  readonly guardando = signal(false);
  readonly cargando = signal(false);
  readonly cargaFallida = signal(false);
  readonly mensaje = signal('');
  private readonly sIdPersona = inject(PersonaService);
  readonly opcionesIdPersona = signal<Persona[]>([]);
  readonly busquedaIdPersona = new FormControl('', { nonNullable: true });
  private readonly sIdColegio = inject(ColegioService);
  readonly opcionesIdColegio = signal<Colegio[]>([]);
  readonly busquedaIdColegio = new FormControl('', { nonNullable: true });
  readonly form = inject(FormBuilder).group({
    titulo: ['', [Validators.required, noVacio, Validators.maxLength(150)]],
    descripcion: [
      '',
      [Validators.required, noVacio, Validators.maxLength(2000)],
    ],
    fecha: ['', [Validators.required]],
    idPersona: [null as number | null, [Validators.required]],
    idColegio: [null as number | null, [Validators.required]],
    estado: ['ABIERTA', [Validators.required]],
    prioridad: ['BAJA', [Validators.required]],
  });
  ngOnInit(): void {
    this.cargarIdPersona();
    this.cargarIdColegio();

    if (this.edicion()) {
      this.cargando.set(true);
      this.service.listId(Number(this.id())).subscribe({
        next: (dto) => {
          this.form.patchValue(dto);
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
          if (dto.idColegio)
            this.sIdColegio.listId(Number(dto.idColegio)).subscribe({
              next: (r) => {
                if (
                  !this.opcionesIdColegio().some(
                    (x) => x.idColegio === r.idColegio,
                  )
                )
                  this.opcionesIdColegio.update((lista) => [r, ...lista]);
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
  cargarIdPersona(): void {
    this.sIdPersona
      .pagina(0, 20, this.busquedaIdPersona.value.trim())
      .subscribe({
        next: (p) => this.opcionesIdPersona.set(p.content),
        error: (err) => this.mensaje.set(mensajeError(err)),
      });
  }
  cargarIdColegio(): void {
    this.sIdColegio
      .pagina(0, 20, this.busquedaIdColegio.value.trim())
      .subscribe({
        next: (p) => this.opcionesIdColegio.set(p.content),
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
    const dto: Incidencia = {
      idIncidencia: this.edicion() ? Number(this.id()) : undefined,
      titulo: (v.titulo ?? '').trim(),
      descripcion: (v.descripcion ?? '').trim(),
      fecha: (v.fecha ?? '').trim(),
      idPersona: Number(v.idPersona),
      idColegio: Number(v.idColegio),
      estado: (v.estado ?? '').trim(),
      prioridad: (v.prioridad ?? '').trim(),
    };
    this.guardando.set(true);
    this.mensaje.set('');
    const peticion = this.edicion()
      ? this.service.actualizar(dto)
      : this.service.insertar(dto);
    peticion.subscribe({
      next: () => {
        this.snack.open('Registro guardado', 'Cerrar', { duration: 3500 });
        void this.router.navigate(['/incidencias']);
      },
      error: (err) => {
        this.guardando.set(false);
        this.mensaje.set(mensajeError(err));
      },
    });
  }
}
