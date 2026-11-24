import { DatePipe } from '@angular/common';
import { Component, input } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MaterialDTO } from '../../../models/material';

/**
 * Tabla de materiales de solo lectura, compartida por las consultas (por título y por curso).
 * El enlace al archivo se abre en otra pestaña y lo anuncia a los lectores de pantalla.
 */
@Component({
  selector: 'app-material-tabla',
  imports: [MatTableModule, DatePipe],
  template: `
    <div class="tabla-contenedor">
      <table mat-table [dataSource]="materiales()">
        <caption class="solo-lector">
          {{
            titulo()
          }}
        </caption>
        <ng-container matColumnDef="titulo">
          <th mat-header-cell *matHeaderCellDef>Título</th>
          <td mat-cell *matCellDef="let m">{{ m.titulo }}</td>
        </ng-container>
        <ng-container matColumnDef="tipo">
          <th mat-header-cell *matHeaderCellDef>Tipo</th>
          <td mat-cell *matCellDef="let m">{{ m.tipo || '—' }}</td>
        </ng-container>
        <ng-container matColumnDef="fechaPublicacion">
          <th mat-header-cell *matHeaderCellDef>Publicado</th>
          <td mat-cell *matCellDef="let m">
            {{
              m.fechaPublicacion
                ? (m.fechaPublicacion | date: 'dd/MM/yyyy')
                : '—'
            }}
          </td>
        </ng-container>
        <ng-container matColumnDef="urlArchivo">
          <th mat-header-cell *matHeaderCellDef>Archivo</th>
          <td mat-cell *matCellDef="let m">
            @if (m.urlArchivo) {
              <a
                [href]="m.urlArchivo"
                target="_blank"
                rel="noopener noreferrer"
              >
                Abrir<span class="solo-lector">
                  {{ m.titulo }} (se abre en una pestaña nueva)</span
                >
              </a>
            } @else {
              —
            }
          </td>
        </ng-container>
        <tr mat-header-row *matHeaderRowDef="columnas"></tr>
        <tr mat-row *matRowDef="let row; columns: columnas"></tr>
        <tr class="mat-mdc-row" *matNoDataRow>
          <td class="mat-mdc-cell sin-datos" [attr.colspan]="columnas.length">
            {{ sinDatos() }}
          </td>
        </tr>
      </table>
    </div>
  `,
})
export class MaterialTabla {
  readonly materiales = input.required<MaterialDTO[]>();
  readonly titulo = input('Materiales');
  readonly sinDatos = input<string | null>('No hay materiales.');

  protected readonly columnas = [
    'titulo',
    'tipo',
    'fechaPublicacion',
    'urlArchivo',
  ];
}
