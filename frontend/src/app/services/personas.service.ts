import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Persona } from '../models/Persona';
import { modulo } from '../models/catalogo';
import { CrudService } from './crud.service';
@Injectable({ providedIn: 'root' })
export class PersonaService extends CrudService<Persona> {
  constructor(http: HttpClient) {
    super(http, modulo('personas'));
  }
}
