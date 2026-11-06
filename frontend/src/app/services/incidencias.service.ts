import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Incidencia } from '../models/Incidencia';
import { modulo } from '../models/catalogo';
import { CrudService } from './crud.service';
@Injectable({ providedIn: 'root' })
export class IncidenciaService extends CrudService<Incidencia> {
  constructor(http: HttpClient) {
    super(http, modulo('incidencias'));
  }
}
