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
import { Colegio } from '../../../models/Colegio';
import { ColegioService } from '../../../services/colegios.service';
import {
  mensajeCampo,
  noVacio,
  rangoFechas,
} from '../../compartido/validadores';
import { mensajeError } from '../../../services/errores';

@Component({
  selector: 'app-colegios-actualizar',
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
export class ColegioActualizarComponent implements OnInit {
  private readonly service = inject(ColegioService);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);
  readonly id = input<string>();
  readonly edicion = computed(() => !!this.id());
  readonly guardando = signal(false);
  readonly cargando = signal(false);
  readonly cargaFallida = signal(false);
  readonly mensaje = signal('');

  readonly form = inject(FormBuilder).group({
    nombre: ['', [Validators.required, noVacio, Validators.maxLength(30)]],
    departamento: [
      '',
      [Validators.required, noVacio, Validators.maxLength(30)],
    ],
    provincia: ['', [Validators.required, noVacio, Validators.maxLength(30)]],
    distrito: ['', [Validators.required, noVacio, Validators.maxLength(30)]],
    comunidad: ['', [Validators.required, noVacio, Validators.maxLength(30)]],
    tipo_zona: ['', [Validators.required, noVacio, Validators.maxLength(30)]],
  });
  ngOnInit(): void {
    if (this.edicion()) {
      this.cargando.set(true);
      this.service.listId(Number(this.id())).subscribe({
        next: (dto) => {
          this.form.patchValue(dto);
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
    const dto: Colegio = {
      idColegio: this.edicion() ? Number(this.id()) : undefined,
      nombre: (v.nombre ?? '').trim(),
      departamento: (v.departamento ?? '').trim(),
      provincia: (v.provincia ?? '').trim(),
      distrito: (v.distrito ?? '').trim(),
      comunidad: (v.comunidad ?? '').trim(),
      tipo_zona: (v.tipo_zona ?? '').trim(),
    };
    this.guardando.set(true);
    this.mensaje.set('');
    const peticion = this.edicion()
      ? this.service.actualizar(dto)
      : this.service.insertar(dto);
    peticion.subscribe({
      next: () => {
        this.snack.open('Registro guardado', 'Cerrar', { duration: 3500 });
        void this.router.navigate(['/colegios']);
      },
      error: (err) => {
        this.guardando.set(false);
        this.mensaje.set(mensajeError(err));
      },
    });
  }
}
