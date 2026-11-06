import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { ConvenioService } from '../../../services/convenios.service';
import { Convenio } from '../../../models/Convenio';
@Component({
  selector: 'app-convenios-actualizar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './actualizarcomponent.html',
})
export class ConvenioActualizarComponent extends FormularioBase<Convenio> {
  constructor(service: ConvenioService) {
    super(service);
  }
}
