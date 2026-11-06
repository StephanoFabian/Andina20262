import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Colegio } from '../models/Colegio';
import { Pagina } from '../models/modulo';
import { environment } from '../../environments/environment';
@Injectable({ providedIn: 'root' })
export class ColegioService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/api/colegios';
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<Colegio>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<Colegio>(`${this.url}/${id}`);
  }
  insertar(dto: Colegio) {
    return this.http.post<Colegio>(this.url + '', dto);
  }
  actualizar(dto: Colegio) {
    return this.http.put<Colegio>(
      this.url + '/{id}'.replace('{id}', String(dto.idColegio)),
      dto,
    );
  }
  eliminar(dto: Colegio) {
    return this.http.delete(`${this.url}/${dto.idColegio}`, {
      responseType: 'text',
    });
  }
}
