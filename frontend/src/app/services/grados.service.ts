import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Grado } from '../models/Grado';
import { modulo } from '../models/catalogo';
import { CrudService } from './crud.service';
@Injectable({ providedIn: 'root' })
export class GradoService extends CrudService<Grado> {
  constructor(http: HttpClient) {
    super(http, modulo('grados'));
  }
}
