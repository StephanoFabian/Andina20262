import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { MatriculaService } from '../../../services/matriculas.service';
import { Matricula } from '../../../models/Matricula';
@Component({
  selector: 'app-matriculas-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class MatriculaListarComponent extends ListadoBase<Matricula> {
  constructor(service: MatriculaService) {
    super(service);
  }
}
