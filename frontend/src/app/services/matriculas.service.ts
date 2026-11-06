import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Matricula } from '../models/Matricula';
import { Pagina } from '../models/modulo';
import { environment } from '../../environments/environment';
@Injectable({ providedIn: 'root' })
export class MatriculaService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/api/matriculas';
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<Matricula>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<Matricula>(`${this.url}/${id}`);
  }
  insertar(dto: Matricula) {
    return this.http.post<Matricula>(this.url + '', dto);
  }
  actualizar(dto: Matricula) {
    return this.http.put<Matricula>(
      this.url + '/{id}'.replace('{id}', String(dto.idMatricula)),
      dto,
    );
  }
  eliminar(dto: Matricula) {
    return this.http.delete(`${this.url}/${dto.idMatricula}`, {
      responseType: 'text',
    });
  }
}
