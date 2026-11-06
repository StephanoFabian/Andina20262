import { Component } from '@angular/core';
import { ListadoBase, LISTADO_IMPORTS } from '../../../shared/listado.base';
import { PersonaService } from '../../../services/personas.service';
import { Persona } from '../../../models/Persona';
@Component({
  selector: 'app-personas-listar',
  imports: LISTADO_IMPORTS,
  templateUrl: './listarcomponent.html',
})
export class PersonaListarComponent extends ListadoBase<Persona> {
  constructor(service: PersonaService) {
    super(service);
  }
}
