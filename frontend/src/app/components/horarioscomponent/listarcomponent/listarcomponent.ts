import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { HorarioService } from '../../../services/horarios.service';
import { Horario } from '../../../models/Horario';
@Component({
  selector: 'app-horarios-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class HorarioListarComponent extends ListadoBase<Horario> {
  constructor(service: HorarioService) {
    super(service);
  }
}
