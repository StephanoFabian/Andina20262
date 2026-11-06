import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { RolService } from '../../../services/roles.service';
import { Rol } from '../../../models/Rol';
@Component({
  selector: 'app-roles-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class RolListarComponent extends ListadoBase<Rol> {
  constructor(service: RolService) {
    super(service);
  }
}
