import { Curso } from '../../../models/Curso';
import { CursoService } from '../../../services/cursos.service';
import {
  FormControl,
  ReactiveFormsModule,
  FormBuilder,
  Validators,
} from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MaterialDTO } from '../../../models/material';
import { Materialservice } from '../../../services/materialservice';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, RouterLink } from '@angular/router';
import { mensajeError } from '../../../services/errores';
import { Materialcursoservice } from '../../../services/materialcursoservice';
import { mensajeCampo } from '../../compartido/validadores';
/** Asocia un material a un curso (no hay edición: para cambiarlo se quita y se vuelve a asociar) */
@Component({
  selector: 'app-materialcurso-insertar',
  imports: [
    MatInputModule,
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
  private readonly servicioIdMaterial = inject(Materialservice);
  protected readonly listaIdMaterial = signal<MaterialDTO[]>([]);
  protected readonly buscarIdMaterial = new FormControl('', {
    nonNullable: true,
  });
  protected cargarIdMaterial(): void {
    this.servicioIdMaterial
      .pagina(0, 20, this.buscarIdMaterial.value.trim())
      .subscribe({ next: (p) => this.listaIdMaterial.set(p.content) });
  }
  private readonly servicioIdCurso = inject(CursoService);
  protected readonly listaIdCurso = signal<Curso[]>([]);
  protected readonly buscarIdCurso = new FormControl('', { nonNullable: true });
  protected cargarIdCurso(): void {
    this.servicioIdCurso
      .pagina(0, 20, this.buscarIdCurso.value.trim())
      .subscribe({ next: (p) => this.listaIdCurso.set(p.content) });
  }
  constructor() {
    this.cargarIdMaterial();
    this.cargarIdCurso();
  }
}
