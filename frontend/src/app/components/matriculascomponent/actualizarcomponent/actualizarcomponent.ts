import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { MatriculaService } from '../../../services/matriculas.service';
import { Matricula } from '../../../models/Matricula';
@Component({
  selector: 'app-matriculas-actualizar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './actualizarcomponent.html',
})
export class MatriculaActualizarComponent extends FormularioBase<Matricula> {
  constructor(service: MatriculaService) {
    super(service);
  }
}
