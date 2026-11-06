import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { AsignacionDocenteService } from '../../../services/asignaciones.service';
import { AsignacionDocente } from '../../../models/AsignacionDocente';
@Component({
  selector: 'app-asignaciones-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class AsignacionDocenteListarComponent extends ListadoBase<AsignacionDocente> {
  constructor(service: AsignacionDocenteService) {
    super(service);
  }
}
