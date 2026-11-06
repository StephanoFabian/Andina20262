import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/auth.service';
import { mensajeError } from '../../services/errores';
@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './logincomponent.html',
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly cargando = signal(false);
  readonly error = signal('');
  readonly ocultar = signal(true);
  readonly form = inject(FormBuilder).nonNullable.group({
    idPersona: [
      null as number | null,
      [Validators.required, Validators.min(1)],
    ],
    passwordPersona: ['', Validators.required],
  });
  constructor() {
    if (this.auth.valid()) void this.router.navigate(['/inicio']);
  }
  ingresar(): void {
    if (this.cargando()) return;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.cargando.set(true);
    this.error.set('');
    const value = this.form.getRawValue();
    this.auth
      .login(value.idPersona!, value.passwordPersona)
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: () => void this.router.navigate(['/inicio']),
        error: (err) => this.error.set(mensajeError(err)),
      });
  }
}
