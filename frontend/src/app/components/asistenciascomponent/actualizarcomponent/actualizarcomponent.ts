import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { AsistenciaService } from '../../../services/asistencias.service';
import { Asistencia } from '../../../models/Asistencia';
@Component({
  selector: 'app-asistencias-actualizar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './actualizarcomponent.html',
})
export class AsistenciaActualizarComponent extends FormularioBase<Asistencia> {
  constructor(service: AsistenciaService) {
    super(service);
  }
}
