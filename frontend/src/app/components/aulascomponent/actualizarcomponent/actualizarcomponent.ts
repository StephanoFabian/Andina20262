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
import { Aula } from '../../../models/Aula';
import { AulaService } from '../../../services/aulas.service';
import {
  mensajeCampo,
  noVacio,
  rangoFechas,
} from '../../compartido/validadores';
import { mensajeError } from '../../../services/errores';
import { Colegio } from '../../../models/Colegio';
import { ColegioService } from '../../../services/colegios.service';
@Component({
  selector: 'app-aulas-actualizar',
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
export class AulaActualizarComponent implements OnInit {
  private readonly service = inject(AulaService);
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
  readonly form = inject(FormBuilder).group({
    nombre: ['', [Validators.required, noVacio, Validators.maxLength(30)]],
    seccion: ['', [Validators.required, noVacio, Validators.maxLength(30)]],
    capacidad: [
      null as number | null,
      [Validators.required, Validators.min(1)],
    ],
    idColegio: [null as number | null, [Validators.required]],
  });
  ngOnInit(): void {
    this.cargarIdColegio();

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
    const dto: Aula = {
      idAula: this.edicion() ? Number(this.id()) : undefined,
      nombre: (v.nombre ?? '').trim(),
      seccion: (v.seccion ?? '').trim(),
      capacidad: Number(v.capacidad),
      idColegio: Number(v.idColegio),
    };
    this.guardando.set(true);
    this.mensaje.set('');
    const peticion = this.edicion()
      ? this.service.actualizar(dto)
      : this.service.insertar(dto);
    peticion.subscribe({
      next: () => {
        this.snack.open('Registro guardado', 'Cerrar', { duration: 3500 });
        void this.router.navigate(['/aulas']);
      },
      error: (err) => {
        this.guardando.set(false);
        this.mensaje.set(mensajeError(err));
      },
    });
  }
}
