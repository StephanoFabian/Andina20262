import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { AsignacionDocenteService } from '../../../services/asignaciones.service';
import { AsignacionDocente } from '../../../models/AsignacionDocente';
@Component({
  selector: 'app-asignaciones-insertar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './insertarcomponent.html',
})
export class AsignacionDocenteInsertarComponent extends FormularioBase<AsignacionDocente> {
  constructor(service: AsignacionDocenteService) {
    super(service);
  }
}
