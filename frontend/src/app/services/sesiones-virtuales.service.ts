import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SesionVirtual } from '../models/SesionVirtual';
import { Pagina } from '../models/modulo';
import { environment } from '../../environments/environment';
@Injectable({ providedIn: 'root' })
export class SesionVirtualService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/api/sesiones-virtuales';
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<SesionVirtual>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<SesionVirtual>(`${this.url}/${id}`);
  }
  insertar(dto: SesionVirtual) {
    return this.http.post<SesionVirtual>(this.url + '', dto);
  }
  actualizar(dto: SesionVirtual) {
    return this.http.put<SesionVirtual>(
      this.url + '/{id}'.replace('{id}', String(dto.idSesionVirtual)),
      dto,
    );
  }
  eliminar(dto: SesionVirtual) {
    return this.http.delete(`${this.url}/${dto.idSesionVirtual}`, {
      responseType: 'text',
    });
  }
}
