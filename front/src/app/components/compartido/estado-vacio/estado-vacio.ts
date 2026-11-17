import { Component, computed, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

/**
 * Estado vacío de una tabla: distingue "cargando", "la búsqueda no encontró nada"
 * y "todavía no hay registros". Admite contenido (por ejemplo, un botón de crear).
 *   <app-estado-vacio entidad="incidencias" [cargando]="cargando()" [busqueda]="busqueda.value" />
 * Estilos en src/mejoras.css (.tabla-vacia).
 */
@Component({
  selector: 'app-estado-vacio',
  imports: [MatIconModule],
  template: `
    <div class="tabla-vacia" role="status">
      @if (cargando()) {
        <p class="tabla-vacia-texto">Cargando…</p>
      } @else {
        <span class="tabla-vacia-icono" aria-hidden="true"
          ><mat-icon>{{ buscando() ? 'search_off' : icono() }}</mat-icon></span
        >
        <p class="tabla-vacia-titulo">{{ tituloFinal() }}</p>
        <p class="tabla-vacia-texto">{{ mensajeFinal() }}</p>
        <div class="tabla-vacia-acciones"><ng-content /></div>
      }
    </div>
  `,
})
export class EstadoVacio {
  /** Nombre en plural y minúsculas: "incidencias", "sesiones virtuales" */
  readonly entidad = input('registros');
  readonly cargando = input(false);
  readonly busqueda = input<string | null | undefined>('');
  /** Hay un filtro adicional aplicado (por horas, área, nota…) */
  readonly filtrado = input(false);
  readonly icono = input('inbox');
  readonly titulo = input<string>();
  readonly mensaje = input<string>();

  protected readonly buscando = computed(
    () => !!this.busqueda()?.trim() || this.filtrado(),
  );
  protected readonly tituloFinal = computed(() =>
    this.buscando()
      ? this.busqueda()?.trim()
        ? `Sin resultados para «${this.busqueda()!.trim()}»`
        : 'Ningún registro coincide con el filtro'
      : (this.titulo() ?? `Todavía no hay ${this.entidad()}`),
  );
  protected readonly mensajeFinal = computed(() =>
    this.buscando()
      ? this.busqueda()?.trim()
        ? 'La búsqueda compara el comienzo del nombre. Prueba con otras letras o deja el campo vacío y pulsa Buscar.'
        : 'Cambia los valores del filtro o quítalo para ver todos los registros.'
      : (this.mensaje() ??
        `Cuando se registren ${this.entidad()}, aparecerán en esta lista.`),
  );
}
