import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Aula } from '../models/Aula';
import { Pagina } from '../models/modulo';
import { environment } from '../../environments/environment';
@Injectable({ providedIn: 'root' })
export class AulaService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/api/aula';
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<Aula>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<Aula>(`${this.url}/${id}`);
  }
  insertar(dto: Aula) {
    return this.http.post<Aula>(this.url + '', dto);
  }
  actualizar(dto: Aula) {
    return this.http.put<Aula>(
      this.url + '/{id}'.replace('{id}', String(dto.idAula)),
      dto,
    );
  }
  eliminar(dto: Aula) {
    return this.http.delete(`${this.url}/${dto.idAula}`, {
      responseType: 'text',
    });
  }
}
