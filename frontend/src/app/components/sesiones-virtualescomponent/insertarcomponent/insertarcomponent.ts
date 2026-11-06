import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { SesionVirtualService } from '../../../services/sesiones-virtuales.service';
import { SesionVirtual } from '../../../models/SesionVirtual';
@Component({
  selector: 'app-sesiones-virtuales-insertar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './insertarcomponent.html',
})
export class SesionVirtualInsertarComponent extends FormularioBase<SesionVirtual> {
  constructor(service: SesionVirtualService) {
    super(service);
  }
}
