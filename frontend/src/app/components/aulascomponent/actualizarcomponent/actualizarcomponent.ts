import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { AulaService } from '../../../services/aulas.service';
import { Aula } from '../../../models/Aula';
@Component({
  selector: 'app-aulas-actualizar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './actualizarcomponent.html',
})
export class AulaActualizarComponent extends FormularioBase<Aula> {
  constructor(service: AulaService) {
    super(service);
  }
}
