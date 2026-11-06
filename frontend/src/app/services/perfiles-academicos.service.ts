import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PerfilAcademico } from '../models/PerfilAcademico';
import { modulo } from '../models/catalogo';
import { CrudService } from './crud.service';
@Injectable({ providedIn: 'root' })
export class PerfilAcademicoService extends CrudService<PerfilAcademico> {
  constructor(http: HttpClient) {
    super(http, modulo('perfiles-academicos'));
  }
}
