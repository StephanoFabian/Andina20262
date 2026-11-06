import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { AsignacionDocenteService } from '../../../services/asignaciones.service';
import { AsignacionDocente } from '../../../models/AsignacionDocente';
@Component({
  selector: 'app-asignaciones-actualizar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './actualizarcomponent.html',
})
export class AsignacionDocenteActualizarComponent extends FormularioBase<AsignacionDocente> {
  constructor(service: AsignacionDocenteService) {
    super(service);
  }
}
