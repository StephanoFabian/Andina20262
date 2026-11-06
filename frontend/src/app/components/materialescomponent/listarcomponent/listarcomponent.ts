import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { MaterialService } from '../../../services/materiales.service';
import { Material } from '../../../models/Material';
@Component({
  selector: 'app-materiales-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class MaterialListarComponent extends ListadoBase<Material> {
  constructor(service: MaterialService) {
    super(service);
  }
}
