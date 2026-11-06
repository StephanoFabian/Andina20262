import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { IncidenciaService } from '../../../services/incidencias.service';
import { Incidencia } from '../../../models/Incidencia';
@Component({
  selector: 'app-incidencias-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class IncidenciaListarComponent extends ListadoBase<Incidencia> {
  constructor(service: IncidenciaService) {
    super(service);
  }
}
