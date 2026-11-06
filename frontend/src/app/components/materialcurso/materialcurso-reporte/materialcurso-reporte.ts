import { PaginaEdissonBase } from '../../../shared/pagina-edisson.base';
import { MatPaginatorModule } from '@angular/material/paginator';
import { map } from 'rxjs';
import { Component, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { CursoMaterialesDTO } from '../../../models/curso-materiales';
import { Materialcursoservice } from '../../../services/materialcursoservice';
import { Consulta, SIN_CONSULTAR } from '../../compartido/consulta';
import {
  Barra,
  GraficoBarras,
} from '../../compartido/grafico-barras/grafico-barras';

/** Reporte: cantidad de materiales de cada curso, para decidir a cuáles subir materiales primero */
@Component({
  selector: 'app-materialcurso-reporte',
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
      <h1>Materiales por curso</h1>
      <p class="nota">Los cursos con menos materiales aparecen primero.</p>

      @if (cargando()) {
        <mat-progress-bar mode="indeterminate" aria-label="Cargando reporte" />
      }
      @if (error()) {
        <div class="mensaje-error" role="alert">{{ error() }}</div>
      }

      @if (resultado().datos; as datos) {
        @if (datos.length > 0) {
          <app-grafico-barras
            titulo="Materiales por curso (menos materiales primero)"
            [datos]="barras()"
            digitos="1.0-0"
          />
        }
        <div class="tabla-contenedor">
          <table mat-table [dataSource]="datos">
            <caption class="solo-lector">
              Cantidad de materiales por curso
            </caption>
            <ng-container matColumnDef="curso">
              <th mat-header-cell *matHeaderCellDef>Curso</th>
              <td mat-cell *matCellDef="let r">{{ r.curso }}</td>
            </ng-container>
            <ng-container matColumnDef="cantidadMateriales">
              <th mat-header-cell *matHeaderCellDef class="numero">
                Materiales
              </th>
              <td mat-cell *matCellDef="let r" class="numero">
                @if (r.cantidadMateriales === 0) {
                  <span class="estado estado-alerta">0</span>
                } @else {
                  {{ r.cantidadMateriales }}
                }
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
        <a mat-button routerLink="/materiales-cursos">Volver</a>
      </div>
    </section>
  `,
})
export class MaterialcursoReporte extends PaginaEdissonBase<CursoMaterialesDTO> {
  protected readonly resultado =
    signal<Consulta<CursoMaterialesDTO>>(SIN_CONSULTAR);
  protected readonly columnas = ['curso', 'cantidadMateriales'];
  protected readonly barras = computed<Barra[]>(() =>
    (this.resultado().datos ?? []).map((r) => ({
      etiqueta: r.curso,
      valor: r.cantidadMateriales,
    })),
  );

  private readonly service = inject(Materialcursoservice);
  protected obtenerPagina() {
    return this.service.reporteMaterialesPorCurso(this.page, this.size).pipe(
      map((p) => {
        this.resultado.set({
          datos: p.content,
          aviso: p.content.length ? null : 'Sin datos para mostrar.',
        });
        return p;
      }),
    );
  }
}
