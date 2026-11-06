import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PeriodoAcademico } from '../models/PeriodoAcademico';
import { Pagina } from '../models/modulo';
import { environment } from '../../environments/environment';
@Injectable({ providedIn: 'root' })
export class PeriodoAcademicoService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/api/periodos-academicos';
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<PeriodoAcademico>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<PeriodoAcademico>(`${this.url}/${id}`);
  }
  insertar(dto: PeriodoAcademico) {
    return this.http.post<PeriodoAcademico>(this.url + '', dto);
  }
  actualizar(dto: PeriodoAcademico) {
    return this.http.put<PeriodoAcademico>(
      this.url + '/{id}'.replace('{id}', String(dto.idPeriodo)),
      dto,
    );
  }
  eliminar(dto: PeriodoAcademico) {
    return this.http.delete(`${this.url}/${dto.idPeriodo}`, {
      responseType: 'text',
    });
  }
}
