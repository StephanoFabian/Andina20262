import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DetalleMatricula } from '../models/DetalleMatricula';
import { modulo } from '../models/catalogo';
import { CrudService } from './crud.service';
@Injectable({ providedIn: 'root' })
export class DetalleMatriculaService extends CrudService<DetalleMatricula> {
  constructor(http: HttpClient) {
    super(http, modulo('detalles-matricula'));
  }
}
