import { Component, computed, input } from '@angular/core';

/**
 * Círculo con las iniciales de una persona, para ponerlo junto a su nombre.
 *   <app-avatar [nombres]="row.nombresPersona" [apellidos]="row.apellidosPersona" />
 * Es decorativo (aria-hidden): el nombre completo siempre va escrito al lado.
 * Estilos en src/mejoras.css (.avatar-mini).
 */
@Component({
  selector: 'app-avatar',
  template: `<span class="avatar-mini" aria-hidden="true">{{
    iniciales()
  }}</span>`,
})
export class Avatar {
  readonly nombres = input<string | null | undefined>();
  readonly apellidos = input<string | null | undefined>();

  protected readonly iniciales = computed(() => {
    const nombres = (this.nombres() ?? '').trim().split(/\s+/).filter(Boolean);
    const apellidos = (this.apellidos() ?? '')
      .trim()
      .split(/\s+/)
      .filter(Boolean);
    // Con apellidos: primera letra del nombre y del apellido. Sin ellos: las dos primeras palabras.
    const partes = apellidos.length
      ? [nombres[0], apellidos[0]]
      : nombres.slice(0, 2);
    return (
      partes
        .map((p) => p?.charAt(0) ?? '')
        .join('')
        .toUpperCase() || '?'
    );
  });
}
