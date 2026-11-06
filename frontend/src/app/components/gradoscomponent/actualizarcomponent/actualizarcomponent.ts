import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { GradoService } from '../../../services/grados.service';
import { Grado } from '../../../models/Grado';
@Component({
  selector: 'app-grados-actualizar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './actualizarcomponent.html',
})
export class GradoActualizarComponent extends FormularioBase<Grado> {
  constructor(service: GradoService) {
    super(service);
  }
}
