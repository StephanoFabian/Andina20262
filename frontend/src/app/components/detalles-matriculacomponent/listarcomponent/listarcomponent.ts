import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { DetalleMatriculaService } from '../../../services/detalles-matricula.service';
import { DetalleMatricula } from '../../../models/DetalleMatricula';
@Component({
  selector: 'app-detalles-matricula-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class DetalleMatriculaListarComponent extends ListadoBase<DetalleMatricula> {
  constructor(service: DetalleMatriculaService) {
    super(service);
  }
}
