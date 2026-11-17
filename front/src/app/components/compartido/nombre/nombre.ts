import { Component, computed, inject, input } from '@angular/core';
import { Persona } from '../../../models/Persona';
import { Avatar } from '../avatar/avatar';
import { NombresService, TipoNombre } from '../nombres.service';

/**
 * Muestra el nombre de un registro relacionado en vez de su ID.
 *   <app-nombre tipo="colegio" [id]="row.idColegio" />
 * Si el nombre no se puede obtener, muestra "#4" para no dejar la celda vacía.
 * Las personas llevan además un avatar con sus iniciales.
 * Estilos en src/mejoras.css (.ref, .persona-celda).
 */
@Component({
  selector: 'app-nombre',
  imports: [Avatar],
  template: `@if (persona(); as p) {
      <span class="persona-celda" [attr.title]="nombre().texto"
        ><app-avatar
          [nombres]="p.nombresPersona"
          [apellidos]="p.apellidosPersona"
        /><span class="ref">{{ nombre().texto }}</span></span
      >
    } @else {
      <span
        class="ref"
        [class.ref--cargando]="nombre().estado === 'cargando'"
        [class.ref--falta]="nombre().estado === 'falta'"
        [attr.title]="nombre().estado === 'ok' ? nombre().texto : null"
        >{{ nombre().texto }}</span
      >
    }`,
})
export class Nombre {
  private readonly nombres = inject(NombresService);
  readonly tipo = input.required<TipoNombre>();
  readonly id = input<number | string | null | undefined>();
  protected readonly nombre = computed(() =>
    this.nombres.nombre(this.tipo(), this.id()),
  );
  /** Registro de la persona, solo cuando el tipo es "persona" y ya se conoce su nombre */
  protected readonly persona = computed(() => {
    if (this.tipo() !== 'persona' || this.nombre().estado !== 'ok') return null;
    const id = Number(this.id());
    if (!Number.isFinite(id)) return null;
    return this.nombres.registro<Persona>('persona', id) ?? null;
  });
}
