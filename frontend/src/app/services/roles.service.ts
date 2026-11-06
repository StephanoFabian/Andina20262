import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Rol } from '../models/Rol';
import { Pagina } from '../models/modulo';
import { environment } from '../../environments/environment';
@Injectable({ providedIn: 'root' })
export class RolService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/roles';
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<Rol>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<Rol>(`${this.url}/${id}`);
  }
  insertar(dto: Rol) {
    return this.http.post<Rol>(this.url + '/nuevo', dto);
  }
  actualizar(dto: Rol) {
    return this.http.put<Rol>(
      this.url + '/actualiza'.replace('{id}', String(dto.idTipoPersona)),
      dto,
    );
  }
  eliminar(dto: Rol) {
    return this.http.delete(`${this.url}/${dto.idTipoPersona}`, {
      responseType: 'text',
    });
  }
}
