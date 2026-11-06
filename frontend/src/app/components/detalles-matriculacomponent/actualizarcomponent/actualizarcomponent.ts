import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { DetalleMatriculaService } from '../../../services/detalles-matricula.service';
import { DetalleMatricula } from '../../../models/DetalleMatricula';
@Component({
  selector: 'app-detalles-matricula-actualizar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './actualizarcomponent.html',
})
export class DetalleMatriculaActualizarComponent extends FormularioBase<DetalleMatricula> {
  constructor(service: DetalleMatriculaService) {
    super(service);
  }
}
