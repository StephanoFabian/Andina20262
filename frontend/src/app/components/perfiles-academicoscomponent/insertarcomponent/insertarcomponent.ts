import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { PerfilAcademicoService } from '../../../services/perfiles-academicos.service';
import { PerfilAcademico } from '../../../models/PerfilAcademico';
@Component({
  selector: 'app-perfiles-academicos-insertar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './insertarcomponent.html',
})
export class PerfilAcademicoInsertarComponent extends FormularioBase<PerfilAcademico> {
  constructor(service: PerfilAcademicoService) {
    super(service);
  }
}
