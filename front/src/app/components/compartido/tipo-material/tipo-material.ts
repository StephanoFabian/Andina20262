import { Component, computed, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { tipoMaterial } from '../iconos';

/**
 * Tipo de un material con su icono (PDF, video, audio, enlace…).
 *   <app-tipo-material [tipo]="m.tipo" />
 * Estilos en src/mejoras.css (.tipo-material).
 */
@Component({
  selector: 'app-tipo-material',
  imports: [MatIconModule],
  template: `<span class="tipo-material"
    ><mat-icon aria-hidden="true">{{ dato().icono }}</mat-icon
    >{{ dato().texto }}</span
  >`,
})
export class TipoMaterial {
  readonly tipo = input<string | null | undefined>();
  protected readonly dato = computed(() => tipoMaterial(this.tipo()));
}
