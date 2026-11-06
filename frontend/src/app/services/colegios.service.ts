import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Colegio } from '../models/Colegio';
import { modulo } from '../models/catalogo';
import { CrudService } from './crud.service';
@Injectable({ providedIn: 'root' })
export class ColegioService extends CrudService<Colegio> {
  constructor(http: HttpClient) {
    super(http, modulo('colegios'));
  }
}
