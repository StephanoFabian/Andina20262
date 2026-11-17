import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Asistencia } from '../models/Asistencia';
import { Pagina } from '../models/modulo';
import { environment } from '../../environments/environment';
@Injectable({ providedIn: 'root' })
export class AsistenciaService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/api/asistencias';
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<Asistencia>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<Asistencia>(`${this.url}/${id}`);
  }
  insertar(dto: Asistencia) {
    return this.http.post<Asistencia>(this.url + '', dto);
  }
  actualizar(dto: Asistencia) {
    return this.http.put<Asistencia>(
      this.url + '/{id}'.replace('{id}', String(dto.idAsistencia)),
      dto,
    );
  }
  eliminar(dto: Asistencia) {
    return this.http.delete(`${this.url}/${dto.idAsistencia}`, {
      responseType: 'text',
    });
  }
}
