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
import { Persona } from '../../../models/Persona';
import { PersonaService } from '../../../services/personas.service';
import {
  mensajeCampo,
  noVacio,
  rangoFechas,
} from '../../compartido/validadores';
import { mensajeError } from '../../../services/errores';
import { Rol } from '../../../models/Rol';
import { RolService } from '../../../services/roles.service';
@Component({
  selector: 'app-personas-actualizar',
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
export class PersonaActualizarComponent implements OnInit {
  private readonly service = inject(PersonaService);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);
  readonly id = input<string>();
  readonly edicion = computed(() => !!this.id());
  readonly guardando = signal(false);
  readonly cargando = signal(false);
  readonly cargaFallida = signal(false);
  readonly mensaje = signal('');
  private readonly sIdRol = inject(RolService);
  readonly opcionesIdRol = signal<Rol[]>([]);
  readonly busquedaIdRol = new FormControl('', { nonNullable: true });
  readonly form = inject(FormBuilder).group({
    nombresPersona: [
      '',
      [Validators.required, noVacio, Validators.maxLength(150)],
    ],
    apellidosPersona: [
      '',
      [Validators.required, noVacio, Validators.maxLength(150)],
    ],
    fechaNacimientoPersona: ['', [Validators.required]],
    emailPersona: [
      '',
      [Validators.required, Validators.maxLength(150), Validators.email],
    ],
    estadoPersona: ['ACTIVO', [Validators.required]],
    idRol: [null as number | null, [Validators.required]],
    passwordPersona: ['', [Validators.minLength(6)]],
  });
  ngOnInit(): void {
    this.cargarIdRol();
    if (!this.edicion())
      this.form.controls.passwordPersona.addValidators(Validators.required);
    if (this.edicion()) {
      this.cargando.set(true);
      this.service.listId(Number(this.id())).subscribe({
        next: (dto) => {
          this.form.patchValue(dto);
          if (dto.idRol)
            this.sIdRol.listId(Number(dto.idRol)).subscribe({
              next: (r) => {
                if (
                  !this.opcionesIdRol().some(
                    (x) => x.idTipoPersona === r.idTipoPersona,
                  )
                )
                  this.opcionesIdRol.update((lista) => [r, ...lista]);
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
  cargarIdRol(): void {
    this.sIdRol
      .pagina(0, 20, this.busquedaIdRol.value.trim())
      .subscribe({
        next: (p) => this.opcionesIdRol.set(p.content),
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
    const dto: Persona = {
      idPersona: this.edicion() ? Number(this.id()) : undefined,
      nombresPersona: (v.nombresPersona ?? '').trim(),
      apellidosPersona: (v.apellidosPersona ?? '').trim(),
      fechaNacimientoPersona: (v.fechaNacimientoPersona ?? '').trim(),
      emailPersona: (v.emailPersona ?? '').trim(),
      estadoPersona: (v.estadoPersona ?? '').trim(),
      idRol: Number(v.idRol),
      passwordPersona: v.passwordPersona || '',
    };
    this.guardando.set(true);
    this.mensaje.set('');
    const peticion = this.edicion()
      ? this.service.actualizar(dto)
      : this.service.insertar(dto);
    peticion.subscribe({
      next: () => {
        this.snack.open('Registro guardado', 'Cerrar', { duration: 3500 });
        void this.router.navigate(['/personas']);
      },
      error: (err) => {
        this.guardando.set(false);
        this.mensaje.set(mensajeError(err));
      },
    });
  }
}
