import { Component, computed, input } from '@angular/core';
import { textoLegible, tonoDe } from '../formato';

/**
 * Etiqueta de color para estados y prioridades.
 *   <app-chip [valor]="row.estado" />
 *   <app-chip [valor]="row.prioridad" tipo="prioridad" />
 * El color nunca es el único indicador: siempre va con el texto.
 * Estilos en src/mejoras.css (.chip).
 */
@Component({
  selector: 'app-chip',
  template: `@if (texto()) {
      <span [class]="'chip chip--' + tono()"
        ><span class="chip-punto" aria-hidden="true"></span>{{ texto() }}</span
      >
    } @else {
      —
    }`,
})
export class Chip {
  readonly valor = input<string | null | undefined>();
  readonly tipo = input<'estado' | 'prioridad'>('estado');
  /** Texto alternativo si el listado ya tiene su propia traducción */
  readonly etiqueta = input<string | null | undefined>();

  protected readonly texto = computed(
    () => this.etiqueta() || textoLegible(this.valor()),
  );
  protected readonly tono = computed(() => tonoDe(this.valor(), this.tipo()));
}
