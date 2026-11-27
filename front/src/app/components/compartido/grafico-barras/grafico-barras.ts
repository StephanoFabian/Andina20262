import { formatNumber } from '@angular/common';
import { Component, computed, inject, input, LOCALE_ID } from '@angular/core';

export interface Barra {
  etiqueta: string;
  valor: number | null;
}

/**
 * Gráfico de barras horizontales de una sola serie (comparar una magnitud por categoría).
 * - Un solo color (verde primario, validado ≥ 3:1 sobre la superficie): sin leyenda, el título dice qué se mide.
 * - Valor escrito en la punta de cada barra: nada depende del color ni del hover.
 * - Es un complemento visual: se oculta a lectores de pantalla porque la tabla del reporte
 *   tiene los mismos datos (su equivalente accesible).
 */
@Component({
  selector: 'app-grafico-barras',
  template: `
    <figure class="grafico">
      <figcaption class="titulo">{{ titulo() }}</figcaption>
      @if (recortado()) {
        <p class="aviso">
          Se muestran los primeros {{ limite() }} de {{ datos().length }}; la
          tabla tiene todos.
        </p>
      }
      <div class="barras" aria-hidden="true">
        @for (b of barras(); track $index) {
          <div class="fila" [title]="b.etiqueta + ': ' + b.texto">
            <span class="etiqueta">{{ b.etiqueta }}</span>
            <span class="pista">
              <span class="barra" [style.--fraccion]="b.fraccion"></span>
              <span class="valor">{{ b.texto }}</span>
            </span>
          </div>
        }
      </div>
    </figure>
  `,
  styles: `
    .grafico {
      margin: 0 0 var(--espacio-3);
      padding: var(--espacio-2);
      background-color: var(--andina-superficie);
      border: 1px solid var(--andina-borde);
      border-radius: 8px;
    }
    .titulo {
      margin-bottom: var(--espacio-2);
      font-weight: 500;
      color: var(--andina-texto);
    }
    .aviso {
      margin: 0 0 var(--espacio-1);
      font-size: 0.8125rem;
      color: var(--andina-texto-secundario);
    }
    .barras {
      display: grid;
      gap: var(--espacio-1);
    }
    .fila {
      display: grid;
      grid-template-columns: minmax(96px, 32%) 1fr;
      align-items: center;
      gap: var(--espacio-2);
      font-size: 0.875rem;
    }
    .etiqueta {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      text-align: right;
      color: var(--andina-texto);
    }
    /* Línea base: hairline del mismo gris que los bordes */
    .pista {
      display: flex;
      align-items: center;
      gap: var(--espacio-1);
      min-height: 24px;
      border-left: 1px solid var(--andina-borde);
    }
    /* Barra ≤ 24px, extremo de datos redondeado 4px, recta en la línea base.
       Deja 72px para el valor, que nunca se recorta. */
    .barra {
      height: 20px;
      width: calc((100% - 72px) * var(--fraccion));
      background-color: var(--andina-primario);
      border-radius: 0 4px 4px 0;
      transition: opacity 120ms ease;
    }
    .fila:hover .barra {
      opacity: 0.8;
    }
    .valor {
      color: var(--andina-texto-secundario);
      font-variant-numeric: tabular-nums;
      white-space: nowrap;
    }
  `,
})
export class GraficoBarras {
  private readonly locale = inject(LOCALE_ID);

  readonly titulo = input.required<string>();
  readonly datos = input.required<Barra[]>();
  /** Máximo fijo de la escala (p. ej. 5 en Likert o 100 en porcentajes); si no, el mayor valor */
  readonly maximo = input<number>();
  readonly sufijo = input('');
  readonly digitos = input('1.0-1');
  /** Más de ~12 barras dejan de leerse: se recorta y la tabla conserva todo */
  readonly limite = input(12);

  protected readonly recortado = computed(
    () => this.datos().length > this.limite(),
  );

  protected readonly barras = computed(() => {
    const visibles = this.datos().slice(0, this.limite());
    const max =
      this.maximo() ?? Math.max(0, ...visibles.map((b) => b.valor ?? 0));
    return visibles.map((b) => ({
      etiqueta: b.etiqueta,
      fraccion:
        max > 0 && b.valor !== null
          ? Math.min(Math.max(b.valor / max, 0), 1)
          : 0,
      texto:
        b.valor === null
          ? '—'
          : formatNumber(b.valor, this.locale, this.digitos()) + this.sufijo(),
    }));
  });
}
