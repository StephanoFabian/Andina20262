import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { SesionVirtualService } from '../../../services/sesiones-virtuales.service';
import { SesionVirtual } from '../../../models/SesionVirtual';
@Component({
  selector: 'app-sesiones-virtuales-actualizar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './actualizarcomponent.html',
})
export class SesionVirtualActualizarComponent extends FormularioBase<SesionVirtual> {
  constructor(service: SesionVirtualService) {
    super(service);
  }
}
