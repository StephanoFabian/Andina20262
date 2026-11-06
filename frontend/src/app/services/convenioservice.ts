import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { ConvenioDTO } from '../models/convenio';
import { ConvenioPorVencerDTO } from '../models/convenio-por-vencer';
import { Pagina } from '../models/modulo';
import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
@Injectable({ providedIn: 'root' })
export class Convenioservice {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/api/convenios';
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<ConvenioDTO>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<ConvenioDTO>(`${this.url}/${id}`);
  }
  insert(dto: ConvenioDTO) {
    return this.http.post<ConvenioDTO>(this.url + '', dto);
  }
  update(dto: ConvenioDTO) {
    return this.http.put<ConvenioDTO>(
      this.url + '/{id}'.replace('{id}', String(dto.idConvenio)),
      dto,
    );
  }
  delete(id: number) {
    return this.http.delete(`${this.url}/${id}`, { responseType: 'text' });
  }

  reportePorVencer(dias = 90, page = 0, size = 10) {
    return this.http.get<Pagina<ConvenioPorVencerDTO>>(
      `${this.url}/por-vencer/pagina`,
      { params: { dias, page, size } },
    );
  }
}
