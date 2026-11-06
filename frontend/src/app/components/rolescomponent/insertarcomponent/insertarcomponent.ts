import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { RolService } from '../../../services/roles.service';
import { Rol } from '../../../models/Rol';
@Component({
  selector: 'app-roles-insertar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './insertarcomponent.html',
})
export class RolInsertarComponent extends FormularioBase<Rol> {
  constructor(service: RolService) {
    super(service);
  }
}
