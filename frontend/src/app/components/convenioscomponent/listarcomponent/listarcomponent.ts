import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { ConvenioService } from '../../../services/convenios.service';
import { Convenio } from '../../../models/Convenio';
@Component({
  selector: 'app-convenios-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class ConvenioListarComponent extends ListadoBase<Convenio> {
  constructor(service: ConvenioService) {
    super(service);
  }
}
