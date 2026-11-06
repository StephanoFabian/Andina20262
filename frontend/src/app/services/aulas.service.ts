import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Aula } from '../models/Aula';
import { modulo } from '../models/catalogo';
import { CrudService } from './crud.service';
@Injectable({ providedIn: 'root' })
export class AulaService extends CrudService<Aula> {
  constructor(http: HttpClient) {
    super(http, modulo('aulas'));
  }
}
