import { Component, computed, inject, input } from '@angular/core';
import { colorCurso } from '../iconos';
import { NombresService } from '../nombres.service';

/**
 * Nombre de un curso como etiqueta de color. Cada curso tiene siempre el mismo
 * color (sale de su nombre), así se reconoce de un vistazo en cualquier listado.
 *   <app-curso-chip [id]="row.id_curso" />      ← busca el nombre por su ID
 *   <app-curso-chip [nombre]="r.curso" />       ← si la fila ya trae el nombre
 * El color acompaña al texto, nunca lo reemplaza. Estilos en src/mejoras.css (.chip-curso).
 */
@Component({
  selector: 'app-curso-chip',
  template: `@if (dato().estado === 'ok' && dato().texto !== '—') {
      <span [class]="'chip chip-curso chip-curso--' + color()">{{
        dato().texto
      }}</span>
    } @else {
      <span
        class="ref"
        [class.ref--cargando]="dato().estado === 'cargando'"
        [class.ref--falta]="dato().estado === 'falta'"
        >{{ dato().texto }}</span
      >
    }`,
})
export class CursoChip {
  private readonly nombres = inject(NombresService);
  readonly id = input<number | string | null | undefined>();
  readonly nombre = input<string | null | undefined>();

  protected readonly dato = computed(() => {
    const nombre = this.nombre()?.trim();
    return nombre
      ? { texto: nombre, estado: 'ok' as const }
      : this.nombres.nombre('curso', this.id());
  });
  protected readonly color = computed(() => colorCurso(this.dato().texto));
}
