import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { IncidenciaService } from '../../../services/incidencias.service';
import { Incidencia } from '../../../models/Incidencia';
@Component({
  selector: 'app-incidencias-actualizar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './actualizarcomponent.html',
})
export class IncidenciaActualizarComponent extends FormularioBase<Incidencia> {
  constructor(service: IncidenciaService) {
    super(service);
  }
}
