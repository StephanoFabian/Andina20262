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
import { Matricula } from '../../../models/Matricula';
import { MatriculaService } from '../../../services/matriculas.service';
import {
  mensajeCampo,
  noVacio,
  rangoFechas,
} from '../../compartido/validadores';
import { mensajeError } from '../../../services/errores';
import { Colegio } from '../../../models/Colegio';
import { ColegioService } from '../../../services/colegios.service';
import { Persona } from '../../../models/Persona';
import { PersonaService } from '../../../services/personas.service';
@Component({
  selector: 'app-matriculas-insertar',
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
export class MatriculaInsertarComponent implements OnInit {
  private readonly service = inject(MatriculaService);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);
  readonly id = input<string>();
  readonly edicion = computed(() => !!this.id());
  readonly guardando = signal(false);
  readonly cargando = signal(false);
  readonly cargaFallida = signal(false);
  readonly mensaje = signal('');
  private readonly sIdColegio = inject(ColegioService);
  readonly opcionesIdColegio = signal<Colegio[]>([]);
  readonly busquedaIdColegio = new FormControl('', { nonNullable: true });
  private readonly sIdPersona = inject(PersonaService);
  readonly opcionesIdPersona = signal<Persona[]>([]);
  readonly busquedaIdPersona = new FormControl('', { nonNullable: true });
  readonly form = inject(FormBuilder).group({
    idColegio: [null as number | null, [Validators.required]],
    idPersona: [null as number | null, [Validators.required]],
  });
  ngOnInit(): void {
    this.cargarIdColegio();
    this.cargarIdPersona();

    if (this.edicion()) {
      this.cargando.set(true);
      this.service.listId(Number(this.id())).subscribe({
        next: (dto) => {
          this.form.patchValue(dto);
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
  cargarIdColegio(): void {
    this.sIdColegio
      .pagina(0, 20, this.busquedaIdColegio.value.trim())
      .subscribe({
        next: (p) => this.opcionesIdColegio.set(p.content),
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
    const dto: Matricula = {
      idMatricula: this.edicion() ? Number(this.id()) : undefined,
      idColegio: Number(v.idColegio),
      idPersona: Number(v.idPersona),
    };
    this.guardando.set(true);
    this.mensaje.set('');
    const peticion = this.edicion()
      ? this.service.actualizar(dto)
      : this.service.insertar(dto);
    peticion.subscribe({
      next: () => {
        this.snack.open('Registro guardado', 'Cerrar', { duration: 3500 });
        void this.router.navigate(['/matriculas']);
      },
      error: (err) => {
        this.guardando.set(false);
        this.mensaje.set(mensajeError(err));
      },
    });
  }
}
