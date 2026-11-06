import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Horario } from '../models/Horario';
import { Pagina } from '../models/modulo';
import { environment } from '../../environments/environment';
@Injectable({ providedIn: 'root' })
export class HorarioService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/api/horarios';
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<Horario>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<Horario>(`${this.url}/${id}`);
  }
  insertar(dto: Horario) {
    return this.http.post<Horario>(this.url + '', dto);
  }
  actualizar(dto: Horario) {
    return this.http.put<Horario>(
      this.url + '/{id}'.replace('{id}', String(dto.idHorario)),
      dto,
    );
  }
  eliminar(dto: Horario) {
    return this.http.delete(`${this.url}/${dto.idHorario}`, {
      responseType: 'text',
    });
  }
}
