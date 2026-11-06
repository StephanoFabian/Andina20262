import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { PeriodoAcademicoService } from '../../../services/periodos-academicos.service';
import { PeriodoAcademico } from '../../../models/PeriodoAcademico';
@Component({
  selector: 'app-periodos-academicos-actualizar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './actualizarcomponent.html',
})
export class PeriodoAcademicoActualizarComponent extends FormularioBase<PeriodoAcademico> {
  constructor(service: PeriodoAcademicoService) {
    super(service);
  }
}
