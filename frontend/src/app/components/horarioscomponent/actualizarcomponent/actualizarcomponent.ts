import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { HorarioService } from '../../../services/horarios.service';
import { Horario } from '../../../models/Horario';
@Component({
  selector: 'app-horarios-actualizar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './actualizarcomponent.html',
})
export class HorarioActualizarComponent extends FormularioBase<Horario> {
  constructor(service: HorarioService) {
    super(service);
  }
}
