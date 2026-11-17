import { OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { PageEvent } from '@angular/material/paginator';
import { mensajeError as errorPeticion } from '../../../services/errores';
import { MatPaginatorModule } from '@angular/material/paginator';
import { map } from 'rxjs';
import { Component, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { MaterialPorTipoDTO } from '../../../models/material-por-tipo';
import { Materialservice } from '../../../services/materialservice';
import { Consulta, SIN_CONSULTAR } from '../../compartido/consulta';
import {
  Barra,
  GraficoBarras,
} from '../../compartido/grafico-barras/grafico-barras';

/** Reporte: cantidad de materiales de cada tipo */
@Component({
  selector: 'app-material-reporte-tipo',
  imports: [
    MatPaginatorModule,
    MatTableModule,
    MatProgressBarModule,
    MatButtonModule,
    RouterLink,
    GraficoBarras,
  ],
  template: `
    <section class="pagina">
      <h1>Materiales por tipo</h1>

      @if (cargando()) {
        <mat-progress-bar mode="indeterminate" aria-label="Cargando reporte" />
      }
      @if (error()) {
        <div class="mensaje-error" role="alert">{{ error() }}</div>
      }

      @if (resultado().datos; as datos) {
        @if (datos.length > 0) {
          <p class="nota">En esta página: {{ sumaPagina() }} material(es).</p>
          <app-grafico-barras
            titulo="Materiales por tipo"
            [datos]="barras()"
            digitos="1.0-0"
          />
        }
        <div class="tabla-contenedor">
          <table mat-table [dataSource]="datos">
            <caption class="solo-lector">
              Materiales por tipo
            </caption>
            <ng-container matColumnDef="tipo">
              <th mat-header-cell *matHeaderCellDef>Tipo</th>
              <td mat-cell *matCellDef="let r">{{ r.tipo || 'Sin tipo' }}</td>
            </ng-container>
            <ng-container matColumnDef="cantidad">
              <th mat-header-cell *matHeaderCellDef class="numero">Cantidad</th>
              <td mat-cell *matCellDef="let r" class="numero">
                {{ r.cantidad }}
              </td>
            </ng-container>
            <tr mat-header-row *matHeaderRowDef="columnas"></tr>
            <tr mat-row *matRowDef="let row; columns: columnas"></tr>
            <tr class="mat-mdc-row" *matNoDataRow>
              <td
                class="mat-mdc-cell sin-datos"
                [attr.colspan]="columnas.length"
              >
                {{ resultado().aviso }}
              </td>
            </tr>
          </table>
        </div>
      }

      <mat-paginator
        [length]="total()"
        [pageIndex]="page"
        [pageSize]="size"
        [pageSizeOptions]="[10, 25, 50]"
        (page)="paginar($event)"
      />
      <div class="acciones">
        <a mat-button routerLink="/materiales">Volver a materiales</a>
      </div>
    </section>
  `,
})
export class MaterialReporteTipo implements OnInit {
  protected readonly resultado =
    signal<Consulta<MaterialPorTipoDTO>>(SIN_CONSULTAR);
  protected readonly columnas = ['tipo', 'cantidad'];
  protected readonly sumaPagina = computed(() =>
    (this.resultado().datos ?? []).reduce((s, r) => s + r.cantidad, 0),
  );
  protected readonly barras = computed<Barra[]>(() =>
    (this.resultado().datos ?? []).map((r) => ({
      etiqueta: r.tipo || 'Sin tipo',
      valor: r.cantidad,
    })),
  );

  private readonly service = inject(Materialservice);
  protected obtenerPagina() {
    return this.service.reportePorTipo(this.page, this.size).pipe(
      map((p) => {
        this.resultado.set({
          datos: p.content,
          aviso: p.content.length ? null : 'Sin datos para mostrar.',
        });
        return p;
      }),
    );
  }

  protected readonly dataSource = new MatTableDataSource<MaterialPorTipoDTO>(
    [],
  );
  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly total = signal(0);
  protected page = 0;
  protected size = 10;
  protected q = '';
  ngOnInit(): void {
    this.cargar();
  }
  protected cargar(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.obtenerPagina().subscribe({
      next: (p) => {
        this.dataSource.data = p.content;
        this.total.set(p.totalElements);
        this.cargando.set(false);
        if (this.page > 0 && !p.content.length) {
          this.page--;
          this.cargar();
        }
      },
      error: (err) => {
        this.cargando.set(false);
        this.error.set(errorPeticion(err));
      },
    });
  }
  protected paginar(e: PageEvent): void {
    this.page = e.pageIndex;
    this.size = e.pageSize;
    this.cargar();
  }
  protected buscar(texto: string): void {
    this.q = texto.trim().slice(0, 100);
    this.page = 0;
    this.cargar();
  }
}
