import {
  Component,
  OnInit,
  OnDestroy,
  computed,
  inject,
  signal,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { Subscription } from 'rxjs';
import { AuthService } from '../../../core/auth.service';
import { MaterialIA } from '../../../models/MaterialIA';
import { MaterialIAService } from '../../../services/material-ia.service';
import { mensajeError } from '../../../services/errores';

@Component({
  selector: 'app-material-ia',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressBarModule,
  ],
  templateUrl: './material-ia.html',
  styleUrl: './material-ia.css',
})
export class MaterialIAComponent implements OnInit, OnDestroy {
  private readonly service = inject(MaterialIAService);
  private readonly auth = inject(AuthService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  readonly form = inject(FormBuilder).group({
    tema: [
      'Triángulos rectángulos y teorema de Pitágoras',
      [Validators.required, Validators.maxLength(200)],
    ],
    nivel: ['Secundaria', [Validators.required, Validators.maxLength(60)]],
    instrucciones: [
      'Explica un concepto, un ejemplo y ejercicios con medidas coherentes.',
      [Validators.maxLength(1000)],
    ],
  });
  readonly documento = signal<MaterialIA | null>(null);
  readonly error = signal('');
  readonly enviando = signal(false);
  readonly descargando = signal(false);
  readonly figuras = signal<{ nombre: string; url: string }[]>([]);
  readonly trabajando = computed(
    () =>
      !!this.documento() &&
      !['listo', 'error'].includes(this.documento()!.estado),
  );
  readonly estadoTexto = computed(
    () =>
      ({
        pendiente: 'Esperando turno',
        redactando: '1. Llama 70B está redactando el material',
        texto_listo: 'Texto listo. Preparando la segunda IA',
        ilustrando: '2. Preparando figuras y PDF',
        listo: 'Material listo para descargar',
        error: 'No se completó una etapa',
      })[this.documento()?.estado || 'pendiente'],
  );
  private id = '';
  private consultando = false;
  private imagenesCargadas = '';
  private timer?: ReturnType<typeof setInterval>;
  private subs = new Subscription();
  private readonly almacenamiento =
    'andina.material-ia.' + (this.auth.claims()?.sub || '');

  ngOnInit(): void {
    const query = this.route.snapshot.queryParamMap.get('trabajo');
    try {
      this.id = query || sessionStorage.getItem(this.almacenamiento) || '';
    } catch {
      this.id = query || '';
    }
    if (this.id && /^[a-f0-9-]{36}$/.test(this.id)) this.consultar();
    else this.id = '';
    this.timer = setInterval(() => {
      if (this.id && (!this.documento() || this.trabajando())) this.consultar();
    }, 3000);
  }

  generar(): void {
    if (this.enviando() || this.trabajando()) return;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    if (!value.tema?.trim() || !value.nivel?.trim()) {
      this.error.set('Escribe el tema y el nivel educativo.');
      return;
    }
    this.limpiarFiguras();
    this.error.set('');
    this.enviando.set(true);
    this.id = crypto.randomUUID();
    try {
      sessionStorage.setItem(this.almacenamiento, this.id);
    } catch {
      /* La URL permite recuperar el trabajo. */
    }
    void this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { trabajo: this.id },
      replaceUrl: true,
    });
    const dto: MaterialIA = {
      id: this.id,
      tema: value.tema.trim(),
      nivel: value.nivel.trim(),
      instrucciones: value.instrucciones?.trim() || '',
      estado: 'pendiente',
    };
    this.documento.set(dto);
    this.subs.add(
      this.service.generar(dto).subscribe({
        next: (result) => {
          this.documento.set(result);
          this.enviando.set(false);
        },
        error: (err) => {
          this.enviando.set(false);
          this.error.set(mensajeError(err));
          if (
            err instanceof HttpErrorResponse &&
            err.status >= 400 &&
            err.status < 500
          )
            this.olvidar();
        },
      }),
    );
  }

  private consultar(): void {
    if (this.consultando || !this.id) return;
    this.consultando = true;
    this.subs.add(
      this.service.consultar(this.id).subscribe({
        next: (result) => {
          this.documento.set(result);
          this.consultando = false;
          if (result.estado === 'listo' && this.imagenesCargadas !== result.id)
            this.cargarFiguras(result);
        },
        error: (err) => {
          this.consultando = false;
          this.error.set(mensajeError(err));
          if (
            err instanceof HttpErrorResponse &&
            [400, 401, 403, 404].includes(err.status)
          )
            this.olvidar();
        },
      }),
    );
  }

  private olvidar(): void {
    this.id = '';
    this.documento.set(null);
    try {
      sessionStorage.removeItem(this.almacenamiento);
    } catch {
      /* Sin almacenamiento. */
    }
  }

  private cargarFiguras(doc: MaterialIA): void {
    this.imagenesCargadas = doc.id;
    for (const nombre of (doc.archivos || [])
      .filter((name) => /^figura-[1-3]\.png$/.test(name))
      .slice(0, 3)) {
      this.subs.add(
        this.service.archivo(doc.id, nombre).subscribe({
          next: (blob) =>
            this.figuras.update((items) =>
              [...items, { nombre, url: URL.createObjectURL(blob) }].sort(
                (a, b) => a.nombre.localeCompare(b.nombre),
              ),
            ),
          error: (err) => this.error.set(mensajeError(err)),
        }),
      );
    }
  }

  descargar(nombre: string): void {
    if (!this.id || this.descargando()) return;
    this.descargando.set(true);
    this.subs.add(
      this.service.archivo(this.id, nombre).subscribe({
        next: (blob) => {
          const url = URL.createObjectURL(blob),
            link = document.createElement('a');
          link.href = url;
          link.download = nombre;
          link.click();
          setTimeout(() => URL.revokeObjectURL(url), 1000);
          this.descargando.set(false);
        },
        error: (err) => {
          this.error.set(mensajeError(err));
          this.descargando.set(false);
        },
      }),
    );
  }

  private limpiarFiguras(): void {
    this.figuras().forEach((item) => URL.revokeObjectURL(item.url));
    this.figuras.set([]);
    this.imagenesCargadas = '';
  }

  ngOnDestroy(): void {
    if (this.timer) clearInterval(this.timer);
    this.subs.unsubscribe();
    this.limpiarFiguras();
  }
}
