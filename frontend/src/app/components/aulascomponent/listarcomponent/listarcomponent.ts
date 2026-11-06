import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { AulaService } from '../../../services/aulas.service';
import { Aula } from '../../../models/Aula';
@Component({
  selector: 'app-aulas-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class AulaListarComponent extends ListadoBase<Aula> {
  constructor(service: AulaService) {
    super(service);
  }
}
