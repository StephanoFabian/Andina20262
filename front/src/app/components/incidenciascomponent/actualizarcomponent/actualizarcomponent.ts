import {
  Component,
  OnInit,
  computed,
  inject,
  input,
  signal,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Incidencia } from '../../../models/Incidencia';
import { Colegio } from '../../../models/Colegio';
import { Persona } from '../../../models/Persona';
import { IncidenciaService } from '../../../services/incidencias.service';
import { ColegioService } from '../../../services/colegios.service';
import { PersonaService } from '../../../services/personas.service';
import { mensajeCampo, noVacio } from '../../compartido/validadores';
import { mensajeError } from '../../../services/errores';

@Component({
  selector: 'app-incidencias-actualizar',
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
export class IncidenciaActualizarComponent implements OnInit {
  private readonly service = inject(IncidenciaService);
  private readonly colegioService = inject(ColegioService);
  private readonly personaService = inject(PersonaService);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);

  readonly id = input<string>();
  readonly edicion = computed(() => !!this.id());
  readonly guardando = signal(false);
  readonly cargando = signal(false);
  readonly cargaFallida = signal(false);
  readonly mensaje = signal('');

  readonly colegios = signal<Colegio[]>([]);
  readonly personas = signal<Persona[]>([]);

  readonly form = inject(FormBuilder).group({
    titulo: ['', [Validators.required, noVacio, Validators.maxLength(150)]],
    descripcion: ['', [Validators.required, noVacio, Validators.maxLength(2000)]],
    fecha: ['', [Validators.required]],
    estado: ['ABIERTA', [Validators.required]],
    prioridad: ['MEDIA', [Validators.required]],
    idPersona: [null as number | null, [Validators.required]],
    idColegio: [null as number | null, [Validators.required]],
  });

  ngOnInit(): void {
    this.colegioService.pagina(0, 100).subscribe({
      next: (p) => this.colegios.set(p.content),
    });
    this.personaService.pagina(0, 100).subscribe({
      next: (p) => this.personas.set(p.content),
    });

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

  etiquetaPersona(p: Persona): string {
    const raw = p as any;
    const texto = [raw.nombres ?? raw.nombre, raw.apellidos ?? raw.apellido]
      .filter(Boolean)
      .join(' ');
    return texto || `Persona #${raw.idPersona}`;
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
    const dto: Incidencia = {
      idIncidencia: this.edicion() ? Number(this.id()) : undefined,
      titulo: (v.titulo ?? '').trim(),
      descripcion: (v.descripcion ?? '').trim(),
      fecha: v.fecha ?? '',
      estado: (v.estado ?? '').trim(),
      prioridad: (v.prioridad ?? '').trim(),
      idPersona: Number(v.idPersona),
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
        void this.router.navigate(['/incidencias']);
      },
      error: (err) => {
        this.guardando.set(false);
        this.mensaje.set(mensajeError(err));
      },
    });
  }
}