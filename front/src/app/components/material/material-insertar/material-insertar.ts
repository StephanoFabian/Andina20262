import {
  Component,
  computed,
  inject,
  input,
  OnInit,
  signal,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
  FormBuilder,
  FormControl,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, RouterLink } from '@angular/router';
import { MaterialDTO } from '../../../models/material';
import { Persona } from '../../../models/Persona';
import { PersonaService } from '../../../services/personas.service';
import { Loginservice } from '../../../services/loginservice';
import { Materialservice } from '../../../services/materialservice';
import { mensajeError } from '../../../services/errores';
import { mensajeCampo, noVacio } from '../../compartido/validadores';
@Component({
  selector: 'app-material-insertar',
  imports: [
    ReactiveFormsModule,
    MatAutocompleteModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    RouterLink,
  ],
  templateUrl: './material-insertar.html',
})
export class MaterialInsertar implements OnInit {
  private readonly mS = inject(Materialservice);
  private readonly pS = inject(PersonaService);
  private readonly loginService = inject(Loginservice);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);
  readonly id = input<string>();
  protected readonly edicion = computed(() => !!this.id());
  protected readonly guardando = signal(false);
  protected readonly cargando = signal(false);
  protected readonly cargaFallida = signal(false);
  protected readonly mensaje = signal<string | null>(null);
  protected readonly listaIdPersona = signal<Persona[]>([]);
  protected readonly buscarIdPersona = new FormControl('', {
    nonNullable: true,
  });
  private readonly tiposUsados = signal<string[]>([]);
  protected readonly form = inject(FormBuilder).group({
    titulo: ['', [Validators.required, noVacio, Validators.maxLength(200)]],
    descripcion: [''],
    tipo: ['', Validators.maxLength(50)],
    urlArchivo: [
      '',
      [Validators.maxLength(500), Validators.pattern(/^https?:\/\/\S+$/)],
    ],
    fechaPublicacion: [''],
    idPersona: [
      Number(this.loginService.idPersona()) || null,
      Validators.required,
    ],
  });
  private readonly tipoEscrito = toSignal(
    this.form.controls.tipo.valueChanges,
    { initialValue: '' },
  );
  protected readonly sugerenciasTipo = computed(() =>
    this.tiposUsados().filter((t) =>
      t.toLowerCase().includes((this.tipoEscrito() || '').toLowerCase()),
    ),
  );
  ngOnInit(): void {
    this.cargarIdPersona();
    this.mS
      .tipos('')
      .subscribe({ next: (tipos) => this.tiposUsados.set(tipos) });
    const autor = this.form.controls.idPersona.value;
    if (autor) this.incluirAutor(autor);
    if (this.id()) {
      this.cargando.set(true);
      this.mS.listId(Number(this.id())).subscribe({
        next: (m) => {
          this.form.patchValue({
            ...m,
            descripcion: m.descripcion || '',
            tipo: m.tipo || '',
            urlArchivo: m.urlArchivo || '',
            fechaPublicacion: m.fechaPublicacion || '',
          });
          this.incluirAutor(m.idPersona);
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
  protected cargarIdPersona(): void {
    this.pS
      .pagina(0, 20, this.buscarIdPersona.value.trim())
      .subscribe({
        next: (p) => this.listaIdPersona.set(p.content),
        error: (err) => this.mensaje.set(mensajeError(err)),
      });
  }
  private incluirAutor(id: number): void {
    this.pS.listId(id).subscribe({
      next: (p) => {
        if (!this.listaIdPersona().some((x) => x.idPersona === p.idPersona))
          this.listaIdPersona.update((lista) => [p, ...lista]);
      },
    });
  }
  protected error(campo: string): string {
    return mensajeCampo(this.form.get(campo)!);
  }
  protected aceptar(): void {
    if (this.guardando() || this.cargando() || this.cargaFallida()) return;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const dto: MaterialDTO = {
      titulo: (v.titulo || '').trim(),
      descripcion: (v.descripcion || '').trim() || null,
      tipo: (v.tipo || '').trim() || null,
      urlArchivo: (v.urlArchivo || '').trim() || null,
      fechaPublicacion: v.fechaPublicacion || null,
      idPersona: Number(v.idPersona),
    };
    this.guardando.set(true);
    this.mensaje.set(null);
    const peticion = this.edicion()
      ? this.mS.update({ ...dto, idMaterial: Number(this.id()) })
      : this.mS.insert(dto);
    peticion.subscribe({
      next: () => {
        this.snack.open('Material guardado correctamente', 'Cerrar', {
          duration: 3500,
        });
        void this.router.navigate(['/materiales']);
      },
      error: (err) => {
        this.guardando.set(false);
        this.mensaje.set(mensajeError(err));
      },
    });
  }
}
