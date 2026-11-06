import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Convenio } from '../models/Convenio';
import { modulo } from '../models/catalogo';
import { CrudService } from './crud.service';
@Injectable({ providedIn: 'root' })
export class ConvenioService extends CrudService<Convenio> {
  constructor(http: HttpClient) {
    super(http, modulo('convenios'));
  }
}
