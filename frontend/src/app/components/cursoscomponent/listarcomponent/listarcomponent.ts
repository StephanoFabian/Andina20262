import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { CursoService } from '../../../services/cursos.service';
import { Curso } from '../../../models/Curso';
@Component({
  selector: 'app-cursos-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class CursoListarComponent extends ListadoBase<Curso> {
  constructor(service: CursoService) {
    super(service);
  }
}
