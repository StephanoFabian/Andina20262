import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MaterialCurso } from '../models/MaterialCurso';
import { modulo } from '../models/catalogo';
import { CrudService } from './crud.service';
@Injectable({ providedIn: 'root' })
export class MaterialCursoService extends CrudService<MaterialCurso> {
  constructor(http: HttpClient) {
    super(http, modulo('materiales-cursos'));
  }
}
