import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SesionVirtual } from '../models/SesionVirtual';
import { modulo } from '../models/catalogo';
import { CrudService } from './crud.service';
@Injectable({ providedIn: 'root' })
export class SesionVirtualService extends CrudService<SesionVirtual> {
  constructor(http: HttpClient) {
    super(http, modulo('sesiones-virtuales'));
  }
}
