import {
  Component,
  DestroyRef,
  Input,
  OnInit,
  inject,
  signal,
} from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { ErrorStateMatcher } from '@angular/material/core';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  of,
  startWith,
  switchMap,
  tap,
} from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { environment } from '../../environments/environment';
import { modulo } from '../models/catalogo';
import { Fila, Pagina } from '../models/modulo';
@Component({
  selector: 'app-referencia',
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
  ],
  template: ` <mat-form-field appearance="outline"
    ><mat-label>{{ label }}</mat-label
    ><input
      matInput
      [formControl]="query"
      [matAutocomplete]="auto"
      [errorStateMatcher]="matcher"
      (blur)="control.markAsTouched()"
      placeholder="Busca y selecciona un registro"
      autocomplete="off"
    /><mat-autocomplete
      #auto="matAutocomplete"
      [displayWith]="mostrar"
      (optionSelected)="seleccionar($event.option.value)"
    >
      @for (row of opciones(); track row[config.id]) {
        <mat-option [value]="row">{{ mostrar(row) }}</mat-option>
      }
      @if (!opciones().length) {
        <mat-option disabled>{{
          error()
            ? 'No se pudieron cargar los datos'
            : 'Sin resultados para esta búsqueda'
        }}</mat-option>
      }</mat-autocomplete
    ><mat-hint>{{
      error() || 'Busca por el comienzo del nombre. Hasta 20 resultados.'
    }}</mat-hint
    ><mat-error>Selecciona un registro de la lista</mat-error></mat-form-field
  >`,
})
export class ReferenciaComponent implements OnInit {
  @Input({ required: true }) control!: FormControl;
  @Input({ required: true }) resource = '';
  @Input({ required: true }) label = '';
  readonly query = new FormControl<string | Fila>('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  readonly opciones = signal<Fila[]>([]);
  readonly error = signal('');
  private readonly http = inject(HttpClient);
  private readonly destroy = inject(DestroyRef);
  get config() {
    return modulo(this.resource);
  }
  readonly matcher: ErrorStateMatcher = {
    isErrorState: () =>
      this.control.invalid && (this.control.touched || this.query.touched),
  };
  readonly mostrar = (row: Fila | string | null): string => {
    if (!row) return '';
    if (typeof row === 'string') return row;
    const label =
      row['nombre'] ||
      row['nombre_curso'] ||
      row['titulo'] ||
      row['nombresPersona'] ||
      row['detalle'] ||
      (row['id_aula'] ? `Aula ${row['id_aula']}` : null) ||
      (row['nombreColegio'] ? `Matrícula · ${row['nombreColegio']}` : null) ||
      'Registro';
    return `${label}${row['apellidosPersona'] ? ' ' + row['apellidosPersona'] : ''} · #${row[this.config.id]}`;
  };
  ngOnInit(): void {
    this.query.valueChanges
      .pipe(
        startWith(''),
        tap((value) => {
          if (typeof value === 'string')
            this.control.setValue(null, { emitEvent: false });
        }),
        debounceTime(250),
        distinctUntilChanged(),
        switchMap((value) => {
          if (typeof value !== 'string') return of(null);
          this.error.set('');
          return this.http
            .get<Pagina<Fila>>(
              `${environment.base}${this.config.path}/pagina`,
              { params: { page: 0, size: 20, q: value.trim().slice(0, 100) } },
            )
            .pipe(
              catchError(() => {
                this.error.set(
                  'Error de conexión. Vuelve a escribir para reintentar.',
                );
                return of(null);
              }),
            );
        }),
        takeUntilDestroyed(this.destroy),
      )
      .subscribe((page) => {
        if (page) this.opciones.set(page.content);
      });
    this.control.valueChanges
      .pipe(
        startWith(this.control.value),
        distinctUntilChanged(),
        switchMap((id) =>
          typeof id === 'number' && id > 0
            ? this.http
                .get<Fila>(`${environment.base}${this.config.path}/${id}`)
                .pipe(catchError(() => of(null)))
            : of(null),
        ),
        takeUntilDestroyed(this.destroy),
      )
      .subscribe((row) => {
        if (row) this.query.setValue(row, { emitEvent: false });
      });
  }
  seleccionar(row: Fila): void {
    this.control.setValue(row[this.config.id]);
    this.control.markAsDirty();
    this.control.markAsTouched();
  }
}
