import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { MaterialService } from '../../../services/materiales.service';
import { Material } from '../../../models/Material';
@Component({
  selector: 'app-materiales-actualizar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './actualizarcomponent.html',
})
export class MaterialActualizarComponent extends FormularioBase<Material> {
  constructor(service: MaterialService) {
    super(service);
  }
}
