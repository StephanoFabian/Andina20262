import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { PeriodoAcademicoService } from '../../../services/periodos-academicos.service';
import { PeriodoAcademico } from '../../../models/PeriodoAcademico';
@Component({
  selector: 'app-periodos-academicos-insertar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './insertarcomponent.html',
})
export class PeriodoAcademicoInsertarComponent extends FormularioBase<PeriodoAcademico> {
  constructor(service: PeriodoAcademicoService) {
    super(service);
  }
}
