import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { PerfilAcademicoService } from '../../../services/perfiles-academicos.service';
import { PerfilAcademico } from '../../../models/PerfilAcademico';
@Component({
  selector: 'app-perfiles-academicos-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class PerfilAcademicoListarComponent extends ListadoBase<PerfilAcademico> {
  constructor(service: PerfilAcademicoService) {
    super(service);
  }
}
