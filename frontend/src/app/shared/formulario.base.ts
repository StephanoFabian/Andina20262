import { DestroyRef, Directive, OnInit, inject, signal } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
  ValidatorFn,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Campo, Fila } from '../models/modulo';
import { CrudService } from '../services/crud.service';
import { ReferenciaComponent } from './referencia.component';
import { mensajeError } from './error';
export const FORMULARIO_IMPORTS = [
  RouterLink,
  ReactiveFormsModule,
  MatFormFieldModule,
  MatInputModule,
  MatSelectModule,
  MatButtonModule,
  MatProgressBarModule,
  ReferenciaComponent,
];
@Directive()
export class FormularioBase<T extends Fila> implements OnInit {
  readonly route = inject(ActivatedRoute);
  readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);
  private readonly destroy = inject(DestroyRef);
  readonly error = signal('');
  readonly guardando = signal(false);
  readonly cargando = signal(false);
  readonly cargaFallida = signal(false);
  readonly editando = this.route.snapshot.paramMap.has('id');
  readonly id = Number(this.route.snapshot.paramMap.get('id'));
  form = new FormGroup<Record<string, FormControl>>({});
  get config() {
    return this.service.config;
  }
  constructor(protected service: CrudService<T>) {}
  ngOnInit(): void {
    const controls: Record<string, FormControl> = {};
    for (const field of this.config.fields) {
      const rules: ValidatorFn[] = [];
      if (field.required || (field.type === 'password' && !this.editando))
        rules.push(Validators.required);
      if (field.maxLength) rules.push(Validators.maxLength(field.maxLength));
      if (field.minLength) rules.push(Validators.minLength(field.minLength));
      if (field.min !== undefined) rules.push(Validators.min(field.min));
      if (field.max !== undefined) rules.push(Validators.max(field.max));
      if (field.type === 'email') rules.push(Validators.email);
      if (['text', 'textarea'].includes(field.type) && field.required)
        rules.push((c) =>
          typeof c.value === 'string' && !c.value.trim()
            ? { required: true }
            : null,
        );
      if (field.type === 'number' && field.key !== 'notasPA')
        rules.push((c) =>
          c.value !== null && !Number.isSafeInteger(Number(c.value))
            ? { entero: true }
            : null,
        );
      if (field.type === 'url')
        rules.push(
          Validators.pattern(
            field.key === 'enlace'
              ? /^https:\/\/[^\s]+$/
              : /^https?:\/\/[^\s]+$/,
          ),
        );
      if (field.type === 'reference') rules.push(Validators.min(1));
      controls[field.key] = new FormControl(
        field.type === 'select'
          ? this.opcionValor(field.options?.[0] || '')
          : null,
        rules,
      );
    }
    this.form = new FormGroup(controls, {
      validators: (group) => {
        for (const [start, end] of [
          ['fechaInicio', 'fechaFin'],
          ['horaInicio', 'horaFin'],
        ]) {
          const a = group.get(start)?.value,
            b = group.get(end)?.value;
          if (a && b && (start === 'horaInicio' ? b <= a : b < a))
            return { rango: true };
        }
        return null;
      },
    });
    if (this.editando) {
      if (!Number.isSafeInteger(this.id) || this.id < 1) {
        this.cargaFallida.set(true);
        this.error.set('El identificador del registro no es válido.');
        return;
      }
      this.cargando.set(true);
      this.service
        .listId(this.id)
        .pipe(
          finalize(() => this.cargando.set(false)),
          takeUntilDestroyed(this.destroy),
        )
        .subscribe({
          next: (data) => this.form.patchValue(data),
          error: (err) => {
            this.error.set(mensajeError(err));
            this.cargaFallida.set(true);
          },
        });
    }
  }
  control(key: string): FormControl {
    return this.form.controls[key];
  }
  opcionValor(
    option: string | { value: number; label: string },
  ): string | number {
    return typeof option === 'string' ? option : option.value;
  }
  opcionEtiqueta(option: string | { value: number; label: string }): string {
    return typeof option === 'string'
      ? option.replace(/_/g, ' ')
      : option.label;
  }
  errorCampo(field: Campo): string {
    const c = this.control(field.key);
    if (c.hasError('entero')) return 'Introduce un número entero';
    if (c.hasError('required')) return 'Este campo es obligatorio';
    if (c.hasError('email')) return 'Introduce un correo válido';
    if (c.hasError('pattern'))
      return (
        'Introduce una URL válida' +
        (field.key === 'enlace' ? ' con HTTPS' : '')
      );
    if (c.hasError('min') || c.hasError('max'))
      return `Valor permitido: ${field.min ?? 1}–${field.max ?? 'sin límite'}`;
    if (c.hasError('minlength')) return `Mínimo ${field.minLength} caracteres`;
    if (c.hasError('maxlength')) return `Máximo ${field.maxLength} caracteres`;
    return 'Revisa este campo';
  }
  guardar(): void {
    if (this.guardando() || this.cargando() || this.cargaFallida()) return;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Revisa los campos marcados antes de guardar.');
      return;
    }
    const data: Fila = { ...this.form.getRawValue() };
    for (const field of this.config.fields) {
      if (typeof data[field.key] === 'string' && field.type !== 'password')
        data[field.key] = String(data[field.key]).trim();
    }
    if (this.editando) data[this.config.id] = this.id;
    this.guardando.set(true);
    this.error.set('');
    const request = this.editando
      ? this.service.actualizar(data as T)
      : this.service.insertar(data as T);
    request
      .pipe(
        finalize(() => this.guardando.set(false)),
        takeUntilDestroyed(this.destroy),
      )
      .subscribe({
        next: () => {
          this.snack.open('Registro guardado correctamente', 'Cerrar', {
            duration: 3500,
          });
          void this.router.navigate(['/', this.config.key]);
        },
        error: (err) => this.error.set(mensajeError(err)),
      });
  }
}
