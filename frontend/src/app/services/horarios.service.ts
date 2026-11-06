import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Horario } from '../models/Horario';
import { modulo } from '../models/catalogo';
import { CrudService } from './crud.service';
@Injectable({ providedIn: 'root' })
export class HorarioService extends CrudService<Horario> {
  constructor(http: HttpClient) {
    super(http, modulo('horarios'));
  }
}
