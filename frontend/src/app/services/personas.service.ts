import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Persona } from '../models/Persona';
import { Pagina } from '../models/modulo';
import { environment } from '../../environments/environment';
@Injectable({ providedIn: 'root' })
export class PersonaService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/personas';
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<Persona>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<Persona>(`${this.url}/${id}`);
  }
  insertar(dto: Persona) {
    return this.http.post<Persona>(this.url + '/nuevo', dto);
  }
  actualizar(dto: Persona) {
    return this.http.put<Persona>(
      this.url + '/actualiza'.replace('{id}', String(dto.idPersona)),
      dto,
    );
  }
  eliminar(dto: Persona) {
    return this.http.delete(`${this.url}/${dto.idPersona}`, {
      responseType: 'text',
    });
  }
}
