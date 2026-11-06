import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { GradoService } from '../../../services/grados.service';
import { Grado } from '../../../models/Grado';
@Component({
  selector: 'app-grados-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class GradoListarComponent extends ListadoBase<Grado> {
  constructor(service: GradoService) {
    super(service);
  }
}
