import { Component, input } from '@angular/core';

export interface Kpi {
  etiqueta: string;
  valor: string;
  detalle: string;
  /** Resalta la tarjeta en rojo (p. ej. cursos sin materiales o convenios por vencer) */
  alerta?: boolean;
}

/**
 * Fila de tarjetas KPI de los dashboards.
 *   <app-tarjetas-kpi titulo="Indicadores de convenios" [kpis]="kpis()" />
 * Estilos en src/edisson.css (sección Dashboards: .kpis, .kpi, .kpi--alerta).
 */
@Component({
  selector: 'app-tarjetas-kpi',
  template: `
    <div class="kpis" role="list" [attr.aria-label]="titulo()">
      @for (k of kpis(); track k.etiqueta) {
        <article class="kpi" [class.kpi--alerta]="k.alerta" role="listitem">
          <p class="kpi-etiqueta">{{ k.etiqueta }}</p>
          <p class="kpi-valor">{{ k.valor }}</p>
          <p class="kpi-detalle">{{ k.detalle }}</p>
        </article>
      }
    </div>
  `,
})
export class TarjetasKpi {
  readonly titulo = input.required<string>();
  readonly kpis = input.required<Kpi[]>();
}
