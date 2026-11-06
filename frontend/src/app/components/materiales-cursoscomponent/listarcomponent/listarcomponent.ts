import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { MaterialCursoService } from '../../../services/materiales-cursos.service';
import { MaterialCurso } from '../../../models/MaterialCurso';
@Component({
  selector: 'app-materiales-cursos-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class MaterialCursoListarComponent extends ListadoBase<MaterialCurso> {
  constructor(service: MaterialCursoService) {
    super(service);
  }
}
