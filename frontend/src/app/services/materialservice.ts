import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { MaterialDTO } from '../models/material';
import { MaterialPorTipoDTO } from '../models/material-por-tipo';
import { Pagina } from '../models/modulo';
import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
@Injectable({ providedIn: 'root' })
export class Materialservice {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/materiales';
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<MaterialDTO>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<MaterialDTO>(`${this.url}/${id}`);
  }
  insert(dto: MaterialDTO) {
    return this.http.post<MaterialDTO>(this.url + '/nuevo', dto);
  }
  update(dto: MaterialDTO) {
    return this.http.put<MaterialDTO>(
      this.url + '/actualiza'.replace('{id}', String(dto.idMaterial)),
      dto,
    );
  }
  delete(id: number) {
    return this.http.delete(`${this.url}/${id}`, { responseType: 'text' });
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
