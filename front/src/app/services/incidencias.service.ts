import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Incidencia } from '../models/Incidencia';
import { Pagina } from '../models/modulo';
import { environment } from '../../environments/environment';
@Injectable({ providedIn: 'root' })
export class IncidenciaService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/api/incidencias';
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<Incidencia>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<Incidencia>(`${this.url}/${id}`);
  }
  insertar(dto: Incidencia) {
    return this.http.post<Incidencia>(this.url + '', dto);
  }
  actualizar(dto: Incidencia) {
    return this.http.put<Incidencia>(
      this.url + '/{id}'.replace('{id}', String(dto.idIncidencia)),
      dto,
    );
  }
  eliminar(dto: Incidencia) {
    return this.http.delete(`${this.url}/${dto.idIncidencia}`, {
      responseType: 'text',
    });
  }
}
