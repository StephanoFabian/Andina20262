import { DestroyRef, Directive, OnInit, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  Subject,
  catchError,
  debounceTime,
  distinctUntilChanged,
  finalize,
  of,
  switchMap,
  tap,
} from 'rxjs';
import { AuthService } from '../core/auth.service';
import { Fila } from '../models/modulo';
import { CrudService } from '../services/crud.service';
import { ConfirmarComponent } from './confirmar.component';
import { mensajeError } from './error';
export const LISTADO_IMPORTS = [
  RouterLink,
  ReactiveFormsModule,
  MatTableModule,
  MatPaginatorModule,
  MatFormFieldModule,
  MatInputModule,
  MatIconModule,
  MatButtonModule,
  MatProgressBarModule,
];
@Directive()
export class ListadoBase<T extends Fila> implements OnInit {
  readonly auth = inject(AuthService);
  private readonly dialog = inject(MatDialog);
  private readonly snack = inject(MatSnackBar);
  private readonly destroy = inject(DestroyRef);
  readonly filas = signal<T[]>([]);
  readonly total = signal(0);
  readonly cargando = signal(false);
  readonly ocupado = signal(false);
  readonly error = signal('');
  readonly busqueda = new FormControl('', { nonNullable: true });
  private readonly solicitudes = new Subject<void>();
  page = 0;
  size = 10;
  get config() {
    return this.service.config;
  }
  get columnas() {
    return [
      { key: this.config.id, label: 'ID' },
      ...this.config.fields.filter(
        (f) =>
          f.key !== this.config.id &&
          f.type !== 'password' &&
          f.type !== 'textarea',
      ),
    ];
  }
  get displayedColumns() {
    return [...this.columnas.map((c) => c.key), 'acciones'];
  }
  constructor(protected service: CrudService<T>) {}
  ngOnInit(): void {
    this.solicitudes
      .pipe(
        switchMap(() => {
          this.cargando.set(true);
          this.error.set('');
          return this.service
            .pagina(this.page, this.size, this.busqueda.value.trim())
            .pipe(
              catchError((err) => {
                this.error.set(mensajeError(err));
                return of(null);
              }),
              finalize(() => this.cargando.set(false)),
            );
        }),
        takeUntilDestroyed(this.destroy),
      )
      .subscribe((data) => {
        if (!data) {
          this.filas.set([]);
          this.total.set(0);
          return;
        }
        if (
          this.page > 0 &&
          data.content.length === 0 &&
          data.totalElements <= this.page * this.size
        ) {
          this.page = Math.max(
            0,
            Math.ceil(data.totalElements / this.size) - 1,
          );
          this.cargar();
          return;
        }
        this.filas.set(data.content);
        this.total.set(data.totalElements);
      });
    this.busqueda.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroy),
      )
      .subscribe(() => {
        this.page = 0;
        this.cargar();
      });
    this.cargar();
  }
  cargar(): void {
    this.solicitudes.next();
  }
  puedeEscribir(): boolean {
    return this.auth.puedeEscribir(this.config);
  }
  paginar(event: PageEvent): void {
    this.page = event.pageIndex;
    this.size = event.pageSize;
    this.cargar();
  }
  mostrar(value: unknown, key: string): string {
    if (value === null || value === undefined || value === '') return '—';
    if (key === 'diaSemana')
      return (
        [
          '',
          'Lunes',
          'Martes',
          'Miércoles',
          'Jueves',
          'Viernes',
          'Sábado',
          'Domingo',
        ][Number(value)] || String(value)
      );
    return String(value).replace(/_/g, ' ');
  }
  eliminar(row: T): void {
    if (this.ocupado() || !this.puedeEscribir()) return;
    const name = String(
      row['nombre'] ||
        row['titulo'] ||
        row['nombresPersona'] ||
        `el registro #${row[this.config.id]}`,
    );
    this.dialog
      .open(ConfirmarComponent, {
        data: name,
        width: '400px',
        maxWidth: '95vw',
      })
      .afterClosed()
      .pipe(takeUntilDestroyed(this.destroy))
      .subscribe((ok) => {
        if (!ok) return;
        this.ocupado.set(true);
        this.service
          .eliminar(row)
          .pipe(
            finalize(() => this.ocupado.set(false)),
            takeUntilDestroyed(this.destroy),
          )
          .subscribe({
            next: () => {
              this.snack.open('Registro eliminado', 'Cerrar', {
                duration: 3500,
              });
              this.cargar();
            },
            error: (err) => this.error.set(mensajeError(err)),
          });
      });
  }
}
