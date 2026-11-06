import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { ColegioService } from '../../../services/colegios.service';
import { Colegio } from '../../../models/Colegio';
@Component({
  selector: 'app-colegios-insertar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './insertarcomponent.html',
})
export class ColegioInsertarComponent extends FormularioBase<Colegio> {
  constructor(service: ColegioService) {
    super(service);
  }
}
