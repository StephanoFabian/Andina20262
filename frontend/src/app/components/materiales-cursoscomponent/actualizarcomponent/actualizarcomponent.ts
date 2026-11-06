import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { MaterialCursoService } from '../../../services/materiales-cursos.service';
import { MaterialCurso } from '../../../models/MaterialCurso';
@Component({
  selector: 'app-materiales-cursos-actualizar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './actualizarcomponent.html',
})
export class MaterialCursoActualizarComponent extends FormularioBase<MaterialCurso> {
  constructor(service: MaterialCursoService) {
    super(service);
  }
}
