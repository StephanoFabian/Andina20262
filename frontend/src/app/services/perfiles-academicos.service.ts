import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PerfilAcademico } from '../models/PerfilAcademico';
import { Pagina } from '../models/modulo';
import { environment } from '../../environments/environment';
@Injectable({ providedIn: 'root' })
export class PerfilAcademicoService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/perfiles-academicos';
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<PerfilAcademico>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<PerfilAcademico>(`${this.url}/${id}`);
  }
  insertar(dto: PerfilAcademico) {
    return this.http.post<PerfilAcademico>(this.url + '/nuevo', dto);
  }
  actualizar(dto: PerfilAcademico) {
    return this.http.put<PerfilAcademico>(
      this.url + '/actualiza'.replace('{id}', String(dto.idPerfilAcademico)),
      dto,
    );
  }
  eliminar(dto: PerfilAcademico) {
    return this.http.delete(`${this.url}/${dto.idPerfilAcademico}`, {
      responseType: 'text',
    });
  }
}
