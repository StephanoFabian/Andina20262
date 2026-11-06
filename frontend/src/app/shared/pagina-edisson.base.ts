import { DestroyRef, Directive, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatTableDataSource } from '@angular/material/table';
import { PageEvent } from '@angular/material/paginator';
import {
  Observable,
  Subject,
  catchError,
  debounceTime,
  finalize,
  of,
  switchMap,
} from 'rxjs';
import { Pagina } from '../models/modulo';
import { mensajeError } from './error';
@Directive()
export abstract class PaginaEdissonBase<T> implements OnInit {
  protected readonly dataSource = new MatTableDataSource<T>([]);
  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly total = signal(0);
  protected page = 0;
  protected size = 10;
  protected q = '';
  private readonly solicitudes = new Subject<void>();
  private readonly busquedas = new Subject<void>();
  private readonly destroy = inject(DestroyRef);
  protected abstract obtenerPagina(): Observable<Pagina<T>>;
  ngOnInit(): void {
    this.solicitudes
      .pipe(
        switchMap(() => {
          this.cargando.set(true);
          this.error.set(null);
          return this.obtenerPagina().pipe(
            catchError((err) => {
              this.error.set(mensajeError(err));
              return of(null);
            }),
            finalize(() => this.cargando.set(false)),
          );
        }),
        takeUntilDestroyed(this.destroy),
      )
      .subscribe((result) => {
        if (!result) {
          this.dataSource.data = [];
          this.total.set(0);
          return;
        }
        if (
          this.page > 0 &&
          !result.content.length &&
          result.totalElements <= this.page * this.size
        ) {
          this.page = Math.max(
            0,
            Math.ceil(result.totalElements / this.size) - 1,
          );
          this.cargar();
          return;
        }
        this.dataSource.data = result.content;
        this.total.set(result.totalElements);
      });
    this.busquedas
      .pipe(debounceTime(300), takeUntilDestroyed(this.destroy))
      .subscribe(() => this.cargar());
    this.cargar();
  }
  protected cargar(): void {
    this.solicitudes.next();
  }
  protected buscar(texto: string): void {
    this.q = texto.trim().slice(0, 100);
    this.page = 0;
    this.busquedas.next();
  }
  protected paginar(event: PageEvent): void {
    this.page = event.pageIndex;
    this.size = event.pageSize;
    this.cargar();
  }
}
