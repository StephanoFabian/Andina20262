import { DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, debounceTime, of, startWith, switchMap } from 'rxjs';
import { ReferenciaComponent } from '../../../shared/referencia.component';
import {
  Component,
  computed,
  inject,
  input,
  OnInit,
  signal,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, RouterLink } from '@angular/router';
import { MaterialDTO } from '../../../models/material';
import { mensajeError } from '../../../services/errores';
import { Loginservice } from '../../../services/loginservice';
import { Materialservice } from '../../../services/materialservice';
import { mensajeCampo, noVacio } from '../../compartido/validadores';

type Campo =
  | 'titulo'
  | 'descripcion'
  | 'tipo'
  | 'urlArchivo'
  | 'fechaPublicacion'
  | 'idPersona';

@Component({
  selector: 'app-material-insertar',
  imports: [
    ReactiveFormsModule,
    ReferenciaComponent,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatAutocompleteModule,
    MatButtonModule,
    RouterLink,
  ],
  templateUrl: './material-insertar.html',
})
export class MaterialInsertar implements OnInit {
  private readonly mS = inject(Materialservice);
  private readonly loginService = inject(Loginservice);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);

  readonly id = input<string>();
  protected readonly edicion = computed(() => !!this.id());

  /** Tipos ya usados en otros materiales, como sugerencias */
  private readonly tiposUsados = signal<string[]>([]);
  protected readonly guardando = signal(false);
  protected readonly cargando = signal(false);
  protected readonly cargaFallida = signal(false);
  protected readonly mensaje = signal<string | null>(null);

  protected readonly form = inject(FormBuilder).group({
    titulo: ['', [Validators.required, noVacio, Validators.maxLength(200)]],
    descripcion: [''],
    tipo: ['', [Validators.maxLength(50)]],
    // Igual que el @Pattern del backend
    urlArchivo: [
      '',
      [Validators.maxLength(500), Validators.pattern(/^https?:\/\/\S+$/)],
    ],
    fechaPublicacion: [''],
    // Por defecto, el autor es quien registra el material
    idPersona: [
      Number(this.loginService.idPersona()) || null,
      [Validators.required],
    ],
  });

  private readonly tipoEscrito = toSignal(
    this.form.controls.tipo.valueChanges,
    { initialValue: '' },
  );
  protected readonly sugerenciasTipo = computed(() => {
    const texto = (this.tipoEscrito() ?? '').trim().toLowerCase();
    return this.tiposUsados().filter((t) => t.toLowerCase().includes(texto));
  });

  private readonly destroy = inject(DestroyRef);
  ngOnInit(): void {
    this.form.controls.tipo.valueChanges
      .pipe(
        startWith(''),
        debounceTime(250),
        switchMap((texto) =>
          this.mS.tipos(texto || '').pipe(catchError(() => of([]))),
        ),
        takeUntilDestroyed(this.destroy),
      )
      .subscribe((tipos) => this.tiposUsados.set(tipos));
    const id = this.id();
    if (id) {
      this.cargando.set(true);
      this.mS.listId(Number(id)).subscribe({
        next: (m) => {
          this.cargando.set(false);
          this.form.patchValue({
            titulo: m.titulo,
            descripcion: m.descripcion ?? '',
            tipo: m.tipo ?? '',
            urlArchivo: m.urlArchivo ?? '',
            fechaPublicacion: m.fechaPublicacion ?? '',
            idPersona: m.idPersona,
          });
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
    if (
      campo === 'urlArchivo' &&
      this.form.controls.urlArchivo.hasError('pattern')
    ) {
      return 'Debe ser un enlace que empiece con http:// o https:// y sin espacios.';
    }
    return mensajeCampo(this.form.controls[campo]);
  }

  protected aceptar(): void {
    if (this.guardando() || this.cargando() || this.cargaFallida()) return;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const opcional = (texto: string | null) => {
      const t = (texto ?? '').trim();
      return t === '' ? null : t;
    };
    const dto: MaterialDTO = {
      titulo: (v.titulo ?? '').trim(),
      descripcion: opcional(v.descripcion),
      tipo: opcional(v.tipo),
      urlArchivo: opcional(v.urlArchivo),
      fechaPublicacion: v.fechaPublicacion || null,
      idPersona: Number(v.idPersona),
    };
    const peticion = this.edicion()
      ? this.mS.update({ ...dto, idMaterial: Number(this.id()) })
      : this.mS.insert(dto);

    this.guardando.set(true);
    this.mensaje.set(null);
    peticion.subscribe({
      next: () => {
        this.snack.open(
          this.edicion()
            ? 'Material actualizado correctamente.'
            : 'Material registrado correctamente.',
          'Cerrar',
          { duration: 4000 },
        );
        this.router.navigate(['/materiales']);
      },
      error: (err: unknown) => {
        this.guardando.set(false);
        this.mensaje.set(mensajeError(err));
      },
    });
  }
}
