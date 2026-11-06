import { Component } from '@angular/core';
import {
  FormularioBase,
  FORMULARIO_IMPORTS,
} from '../../../shared/formulario.base';
import { PersonaService } from '../../../services/personas.service';
import { Persona } from '../../../models/Persona';
@Component({
  selector: 'app-personas-insertar',
  imports: FORMULARIO_IMPORTS,
  templateUrl: './insertarcomponent.html',
})
export class PersonaInsertarComponent extends FormularioBase<Persona> {
  constructor(service: PersonaService) {
    super(service);
  }
}
