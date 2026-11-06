import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { CursoService } from '../../../services/cursos.service';
import { Curso } from '../../../models/Curso';
@Component({
  selector: 'app-cursos-actualizar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './actualizarcomponent.html',
})
export class CursoActualizarComponent extends FormularioBase<Curso> {
  constructor(service: CursoService) {
    super(service);
  }
}
