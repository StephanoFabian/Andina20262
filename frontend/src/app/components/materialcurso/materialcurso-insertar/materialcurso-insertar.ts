import { ReferenciaComponent } from '../../../shared/referencia.component';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, RouterLink } from '@angular/router';

import { mensajeError } from '../../../services/errores';
import { Materialcursoservice } from '../../../services/materialcursoservice';

import { mensajeCampo } from '../../compartido/validadores';

/** Asocia un material a un curso (no hay edición: para cambiarlo se quita y se vuelve a asociar) */
@Component({
  selector: 'app-materialcurso-insertar',
  imports: [
    ReferenciaComponent,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonModule,
    RouterLink,
  ],
  templateUrl: './materialcurso-insertar.html',
})
export class MaterialcursoInsertar {
  private readonly mcS = inject(Materialcursoservice);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);

  protected readonly guardando = signal(false);
  protected readonly cargando = signal(false);
  protected readonly cargaFallida = signal(false);
  protected readonly mensaje = signal<string | null>(null);

  protected readonly form = inject(FormBuilder).group({
    idMaterial: [null as number | null, [Validators.required]],
    idCurso: [null as number | null, [Validators.required]],
  });

  protected error(campo: 'idMaterial' | 'idCurso'): string {
    return mensajeCampo(this.form.controls[campo]);
  }

  protected aceptar(): void {
    if (this.guardando() || this.cargando() || this.cargaFallida()) return;
    const { idMaterial, idCurso } = this.form.getRawValue();
    if (this.form.invalid || idMaterial === null || idCurso === null) {
      this.form.markAllAsTouched();
      return;
    }
    this.guardando.set(true);
    this.mensaje.set(null);
    this.mcS.insert({ idMaterial, idCurso }).subscribe({
      next: () => {
        this.snack.open('Material asociado al curso.', 'Cerrar', {
          duration: 4000,
        });
        this.router.navigate(['/materiales-cursos']);
      },
      error: (err: unknown) => {
        this.guardando.set(false);
        this.mensaje.set(
          err instanceof HttpErrorResponse && err.status === 409
            ? 'Ese material ya está asociado a ese curso.'
            : mensajeError(err),
        );
      },
    });
  }
}
