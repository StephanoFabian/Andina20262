import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Grado } from '../models/Grado';
import { Pagina } from '../models/modulo';
import { environment } from '../../environments/environment';
@Injectable({ providedIn: 'root' })
export class GradoService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/api/grados';
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<Grado>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<Grado>(`${this.url}/${id}`);
  }
  insertar(dto: Grado) {
    return this.http.post<Grado>(this.url + '', dto);
  }
  actualizar(dto: Grado) {
    return this.http.put<Grado>(
      this.url + '/{id}'.replace('{id}', String(dto.idGrado)),
      dto,
    );
  }
  eliminar(dto: Grado) {
    return this.http.delete(`${this.url}/${dto.idGrado}`, {
      responseType: 'text',
    });
  }
}
