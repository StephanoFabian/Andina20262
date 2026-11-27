import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { finalize } from 'rxjs';
import { RegistroService } from '../../services/registro.service';
import { Persona } from '../../models/Persona';
import { mensajeError } from '../../services/errores';
import {
  correo,
  fechaPasada,
  hoyISO,
  mensajeCampo,
  noVacio,
} from '../compartido/validadores';

/** Validador de grupo: la confirmación debe repetir la contraseña. */
function clavesIguales(grupo: AbstractControl): ValidationErrors | null {
  const clave = grupo.get('passwordPersona')?.value as string;
  const repetir = grupo.get('repetirPassword');
  if (!repetir) return null;
  const otros = { ...(repetir.errors ?? {}) };
  delete otros['distinta'];
  const distinta = !!repetir.value && repetir.value !== clave;
  repetir.setErrors(
    distinta
      ? { ...otros, distinta: true }
      : Object.keys(otros).length > 0
        ? otros
        : null,
  );
  return distinta ? { distinta: true } : null;
}

@Component({
  selector: 'app-registro',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './registrocomponent.html',
})
export class RegistroComponent {
  private readonly service = inject(RegistroService);
  readonly hoy = hoyISO();
  readonly guardando = signal(false);
  readonly error = signal('');
  readonly ocultar = signal(true);
  readonly personaCreada = signal<Persona | null>(null);
  readonly form = inject(FormBuilder).nonNullable.group(
    {
      nombresPersona: [
        '',
        [Validators.required, noVacio, Validators.maxLength(150)],
      ],
      apellidosPersona: [
        '',
        [Validators.required, noVacio, Validators.maxLength(150)],
      ],
      fechaNacimientoPersona: ['', [Validators.required, fechaPasada]],
      emailPersona: [
        '',
        [Validators.required, Validators.maxLength(150), correo],
      ],
      passwordPersona: [
        '',
        [Validators.required, Validators.minLength(8), Validators.maxLength(72)],
      ],
      repetirPassword: ['', [Validators.required]],
    },
    { validators: clavesIguales },
  );
  mensaje(campo: string): string {
    const control = this.form.get(campo);
    return control?.errors?.['distinta']
      ? 'Las contraseñas no coinciden.'
      : mensajeCampo(control);
  }
  registrar(): void {
    if (this.guardando()) return;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.guardando.set(true);
    this.error.set('');
    const { repetirPassword, ...dto } = this.form.getRawValue();
    this.service
      .registrar({
        ...dto,
        nombresPersona: dto.nombresPersona.trim(),
        apellidosPersona: dto.apellidosPersona.trim(),
        emailPersona: dto.emailPersona.trim(),
      })
      .pipe(finalize(() => this.guardando.set(false)))
      .subscribe({
        next: (persona) => this.personaCreada.set(persona),
        error: (err) => this.error.set(mensajeError(err)),
      });
  }
}
