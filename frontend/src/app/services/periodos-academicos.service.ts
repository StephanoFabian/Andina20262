import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PeriodoAcademico } from '../models/PeriodoAcademico';
import { modulo } from '../models/catalogo';
import { CrudService } from './crud.service';
@Injectable({ providedIn: 'root' })
export class PeriodoAcademicoService extends CrudService<PeriodoAcademico> {
  constructor(http: HttpClient) {
    super(http, modulo('periodos-academicos'));
  }
}
