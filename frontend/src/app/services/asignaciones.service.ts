import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AsignacionDocente } from '../models/AsignacionDocente';
import { Pagina } from '../models/modulo';
import { environment } from '../../environments/environment';
@Injectable({ providedIn: 'root' })
export class AsignacionDocenteService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/asignacion';
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<AsignacionDocente>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<AsignacionDocente>(`${this.url}/${id}`);
  }
  insertar(dto: AsignacionDocente) {
    return this.http.post<AsignacionDocente>(this.url + '', dto);
  }
  actualizar(dto: AsignacionDocente) {
    return this.http.put<AsignacionDocente>(
      this.url + ''.replace('{id}', String(dto.id_asignacion)),
      dto,
    );
  }
  eliminar(dto: AsignacionDocente) {
    return this.http.delete(`${this.url}/${dto.id_asignacion}`, {
      responseType: 'text',
    });
  }
}
