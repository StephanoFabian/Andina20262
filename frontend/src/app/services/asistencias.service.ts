import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Asistencia } from '../models/Asistencia';
import { modulo } from '../models/catalogo';
import { CrudService } from './crud.service';
@Injectable({ providedIn: 'root' })
export class AsistenciaService extends CrudService<Asistencia> {
  constructor(http: HttpClient) {
    super(http, modulo('asistencias'));
  }
}
