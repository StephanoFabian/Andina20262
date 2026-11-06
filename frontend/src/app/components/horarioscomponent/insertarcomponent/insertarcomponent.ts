import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { HorarioService } from '../../../services/horarios.service';
import { Horario } from '../../../models/Horario';
@Component({
  selector: 'app-horarios-insertar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './insertarcomponent.html',
})
export class HorarioInsertarComponent extends FormularioBase<Horario> {
  constructor(service: HorarioService) {
    super(service);
  }
}
