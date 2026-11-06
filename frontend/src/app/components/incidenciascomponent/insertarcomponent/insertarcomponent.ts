import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { IncidenciaService } from '../../../services/incidencias.service';
import { Incidencia } from '../../../models/Incidencia';
@Component({
  selector: 'app-incidencias-insertar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './insertarcomponent.html',
})
export class IncidenciaInsertarComponent extends FormularioBase<Incidencia> {
  constructor(service: IncidenciaService) {
    super(service);
  }
}
