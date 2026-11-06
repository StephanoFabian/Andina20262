import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Curso } from '../models/Curso';
import { Pagina } from '../models/modulo';
import { environment } from '../../environments/environment';
@Injectable({ providedIn: 'root' })
export class CursoService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/curso';
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<Curso>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<Curso>(`${this.url}/${id}`);
  }
  insertar(dto: Curso) {
    return this.http.post<Curso>(this.url + '', dto);
  }
  actualizar(dto: Curso) {
    return this.http.put<Curso>(
      this.url + ''.replace('{id}', String(dto.id_curso)),
      dto,
    );
  }
  eliminar(dto: Curso) {
    return this.http.delete(`${this.url}/${dto.id_curso}`, {
      responseType: 'text',
    });
  }
}
