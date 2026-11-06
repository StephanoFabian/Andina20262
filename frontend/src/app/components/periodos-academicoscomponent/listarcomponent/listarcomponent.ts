import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { PeriodoAcademicoService } from '../../../services/periodos-academicos.service';
import { PeriodoAcademico } from '../../../models/PeriodoAcademico';
@Component({
  selector: 'app-periodos-academicos-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class PeriodoAcademicoListarComponent extends ListadoBase<PeriodoAcademico> {
  constructor(service: PeriodoAcademicoService) {
    super(service);
  }
}
