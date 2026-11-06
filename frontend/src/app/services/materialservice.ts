import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { MaterialDTO } from '../models/material';
import { MaterialPorTipoDTO } from '../models/material-por-tipo';
import { Pagina } from '../models/modulo';
import { EdissonApi } from './edisson-api';
@Injectable({ providedIn: 'root' })
export class Materialservice extends EdissonApi<MaterialDTO> {
  constructor() {
    super('/materiales', 'idMaterial');
  }
  tipos(q = '') {
    return this.http.get<string[]>(`${this.url}/tipos`, {
      params: { q: q.slice(0, 100) },
    });
  }
  consulta(page = 0, size = 10, q = '', idCurso?: number) {
    return this.http.get<Pagina<MaterialDTO>>(`${this.url}/consulta/pagina`, {
      params: { page, size, q, ...(idCurso ? { idCurso } : {}) },
    });
  }
  reportePorTipo(page = 0, size = 10) {
    return this.http.get<Pagina<MaterialPorTipoDTO>>(
      `${this.url}/reporte-por-tipo/pagina`,
      { params: { page, size } },
    );
  }
}
