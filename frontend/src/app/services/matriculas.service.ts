import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Matricula } from '../models/Matricula';
import { modulo } from '../models/catalogo';
import { CrudService } from './crud.service';
@Injectable({ providedIn: 'root' })
export class MatriculaService extends CrudService<Matricula> {
  constructor(http: HttpClient) {
    super(http, modulo('matriculas'));
  }
}
