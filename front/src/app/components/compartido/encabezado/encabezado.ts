import { Component, computed, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MODULOS } from '../../../models/catalogo';
import { descripcionModulo, iconoModulo } from '../iconos';

/**
 * Encabezado común de todos los listados: icono del módulo, título, una línea
 * que explica qué es, el total de registros y los botones de la página.
 *   <app-encabezado modulo="cursos" [total]="total()">
 *     <a mat-flat-button routerLink="/cursos/insertar">Nuevo registro</a>
 *   </app-encabezado>
 * El título y la descripción salen del catálogo; se pueden cambiar con
 * [titulo] y [descripcion]. Estilos en src/mejoras.css (.lista-cabecera).
 */
@Component({
  selector: 'app-encabezado',
  imports: [MatIconModule],
  host: { class: 'lista-cabecera-host' },
  template: `
    <header class="lista-cabecera">
      <span class="lista-icono" aria-hidden="true"
        ><mat-icon>{{ icono() }}</mat-icon></span
      >
      <div class="lista-titulo">
        <h1>{{ tituloFinal() }}</h1>
        @if (descripcionFinal()) {
          <p>{{ descripcionFinal() }}</p>
        }
      </div>
      @if (total() !== null && total() !== undefined) {
        <span class="lista-total" aria-live="polite"
          ><b>{{ total() }}</b>
          {{ total() === 1 ? 'registro' : 'registros' }}</span
        >
      }
      <div class="lista-acciones"><ng-content /></div>
    </header>
  `,
})
export class Encabezado {
  /** Clave del módulo en el catálogo: "cursos", "sesiones-virtuales"… */
  readonly modulo = input.required<string>();
  readonly titulo = input<string>();
  readonly descripcion = input<string>();
  readonly total = input<number | null>();

  protected readonly icono = computed(() => iconoModulo(this.modulo()));
  protected readonly tituloFinal = computed(
    () =>
      this.titulo() ??
      MODULOS.find((m) => m.key === this.modulo())?.label ??
      '',
  );
  protected readonly descripcionFinal = computed(
    () => this.descripcion() ?? descripcionModulo(this.modulo()),
  );
}
