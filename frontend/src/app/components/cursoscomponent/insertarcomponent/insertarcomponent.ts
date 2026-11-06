import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { CursoService } from '../../../services/cursos.service';
import { Curso } from '../../../models/Curso';
@Component({
  selector: 'app-cursos-insertar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './insertarcomponent.html',
})
export class CursoInsertarComponent extends FormularioBase<Curso> {
  constructor(service: CursoService) {
    super(service);
  }
}
