import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Rol } from '../models/Rol';
import { modulo } from '../models/catalogo';
import { CrudService } from './crud.service';
@Injectable({ providedIn: 'root' })
export class RolService extends CrudService<Rol> {
  constructor(http: HttpClient) {
    super(http, modulo('roles'));
  }
}
