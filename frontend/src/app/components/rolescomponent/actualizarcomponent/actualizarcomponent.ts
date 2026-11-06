import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { RolService } from '../../../services/roles.service';
import { Rol } from '../../../models/Rol';
@Component({
  selector: 'app-roles-actualizar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './actualizarcomponent.html',
})
export class RolActualizarComponent extends FormularioBase<Rol> {
  constructor(service: RolService) {
    super(service);
  }
}
