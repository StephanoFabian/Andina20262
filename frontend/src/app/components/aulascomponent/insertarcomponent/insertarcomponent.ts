import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { AulaService } from '../../../services/aulas.service';
import { Aula } from '../../../models/Aula';
@Component({
  selector: 'app-aulas-insertar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './insertarcomponent.html',
})
export class AulaInsertarComponent extends FormularioBase<Aula> {
  constructor(service: AulaService) {
    super(service);
  }
}
