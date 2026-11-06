import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { SesionVirtualService } from '../../../services/sesiones-virtuales.service';
import { SesionVirtual } from '../../../models/SesionVirtual';
@Component({
  selector: 'app-sesiones-virtuales-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class SesionVirtualListarComponent extends ListadoBase<SesionVirtual> {
  constructor(service: SesionVirtualService) {
    super(service);
  }
}
