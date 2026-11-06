import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AsignacionDocente } from '../models/AsignacionDocente';
import { modulo } from '../models/catalogo';
import { CrudService } from './crud.service';
@Injectable({ providedIn: 'root' })
export class AsignacionDocenteService extends CrudService<AsignacionDocente> {
  constructor(http: HttpClient) {
    super(http, modulo('asignaciones'));
  }
}
