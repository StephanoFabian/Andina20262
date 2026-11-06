import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { ConvenioService } from '../../../services/convenios.service';
import { Convenio } from '../../../models/Convenio';
@Component({
  selector: 'app-convenios-insertar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './insertarcomponent.html',
})
export class ConvenioInsertarComponent extends FormularioBase<Convenio> {
  constructor(service: ConvenioService) {
    super(service);
  }
}
