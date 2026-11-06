import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { MaterialCursoService } from '../../../services/materiales-cursos.service';
import { MaterialCurso } from '../../../models/MaterialCurso';
@Component({
  selector: 'app-materiales-cursos-insertar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './insertarcomponent.html',
})
export class MaterialCursoInsertarComponent extends FormularioBase<MaterialCurso> {
  constructor(service: MaterialCursoService) {
    super(service);
  }
}
