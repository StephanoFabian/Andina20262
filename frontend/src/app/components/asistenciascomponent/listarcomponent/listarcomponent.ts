import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { AsistenciaService } from '../../../services/asistencias.service';
import { Asistencia } from '../../../models/Asistencia';
@Component({
  selector: 'app-asistencias-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class AsistenciaListarComponent extends ListadoBase<Asistencia> {
  constructor(service: AsistenciaService) {
    super(service);
  }
}
