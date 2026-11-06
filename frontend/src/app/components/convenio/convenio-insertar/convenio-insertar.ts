import { KeyValuePipe } from '@angular/common';
import {
  Component,
  computed,
  inject,
  input,
  OnInit,
  signal,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, RouterLink } from '@angular/router';
import {
  ConvenioDTO,
  ESTADOS_CONVENIO,
  TIPOS_ENTIDAD,
} from '../../../models/convenio';
import { Convenioservice } from '../../../services/convenioservice';
import { mensajeError } from '../../../services/errores';
import {
  digitos,
  mensajeCampo,
  noVacio,
  rangoFechas,
} from '../../compartido/validadores';

type Campo =
  | 'nombre'
  | 'entidad'
  | 'tipoEntidad'
  | 'region'
  | 'fechaInicio'
  | 'fechaFin'
  | 'presupuesto'
  | 'estado';

@Component({
  selector: 'app-convenio-insertar',
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    RouterLink,
    KeyValuePipe,
  ],
  templateUrl: './convenio-insertar.html',
})
export class ConvenioInsertar implements OnInit {
  private readonly cS = inject(Convenioservice);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);

  readonly id = input<string>();
  protected readonly edicion = computed(() => !!this.id());

  protected readonly tiposEntidad = TIPOS_ENTIDAD;
  protected readonly estados = ESTADOS_CONVENIO;
  /** Conserva el orden de declaración en el keyvalue pipe */
  protected readonly sinOrden = () => 0;

  protected readonly guardando = signal(false);
  protected readonly cargando = signal(false);
  protected readonly cargaFallida = signal(false);
  protected readonly mensaje = signal<string | null>(null);

  protected readonly form = inject(FormBuilder).group(
    {
      nombre: ['', [Validators.required, noVacio, Validators.maxLength(200)]],
      entidad: ['', [Validators.required, noVacio, Validators.maxLength(200)]],
      tipoEntidad: ['', [Validators.required]],
      region: ['', [Validators.required, noVacio, Validators.maxLength(50)]],
      fechaInicio: ['', [Validators.required]],
      fechaFin: ['', [Validators.required]],
      presupuesto: [
        null as number | null,
        [Validators.required, Validators.min(0), digitos(12, 2)],
      ],
      estado: ['VIGENTE'],
    },
    { validators: rangoFechas('fechaInicio', 'fechaFin') },
  );

  ngOnInit(): void {
    const id = this.id();
    if (id) {
      this.cargando.set(true);
      this.cS.listId(Number(id)).subscribe({
        next: (c) => {
          this.cargando.set(false);
          this.form.patchValue(c);
        },
        error: (err: unknown) => {
          this.cargando.set(false);
          this.cargaFallida.set(true);
          this.mensaje.set(mensajeError(err));
        },
      });
    }
  }

  protected error(campo: Campo): string {
    return mensajeCampo(this.form.controls[campo]);
  }

  protected largo(campo: 'nombre' | 'entidad' | 'region'): number {
    return this.form.controls[campo].value?.length ?? 0;
  }

  protected aceptar(): void {
    if (this.guardando() || this.cargando() || this.cargaFallida()) return;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const dto: ConvenioDTO = {
      nombre: (v.nombre ?? '').trim(),
      entidad: (v.entidad ?? '').trim(),
      tipoEntidad: v.tipoEntidad ?? '',
      region: (v.region ?? '').trim(),
      fechaInicio: v.fechaInicio ?? '',
      fechaFin: v.fechaFin ?? '',
      presupuesto: Number(v.presupuesto),
      estado: v.estado || null,
    };
    // El PUT /actualiza lleva el id en el cuerpo
    const peticion = this.edicion()
      ? this.cS.update({ ...dto, idConvenio: Number(this.id()) })
      : this.cS.insert(dto);

    this.guardando.set(true);
    this.mensaje.set(null);
    peticion.subscribe({
      next: () => {
        this.snack.open(
          this.edicion()
            ? 'Convenio actualizado correctamente.'
            : 'Convenio registrado correctamente.',
          'Cerrar',
          { duration: 4000 },
        );
        this.router.navigate(['/convenios']);
      },
      error: (err: unknown) => {
        this.guardando.set(false);
        this.mensaje.set(mensajeError(err));
      },
    });
  }
}
