import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { AsistenciaService } from '../../../services/asistencias.service';
import { Asistencia } from '../../../models/Asistencia';
@Component({
  selector: 'app-asistencias-insertar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './insertarcomponent.html',
})
export class AsistenciaInsertarComponent extends FormularioBase<Asistencia> {
  constructor(service: AsistenciaService) {
    super(service);
  }
}
