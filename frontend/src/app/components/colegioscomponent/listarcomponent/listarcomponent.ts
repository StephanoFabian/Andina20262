import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { ColegioService } from '../../../services/colegios.service';
import { Colegio } from '../../../models/Colegio';
@Component({
  selector: 'app-colegios-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class ColegioListarComponent extends ListadoBase<Colegio> {
  constructor(service: ColegioService) {
    super(service);
  }
}
