import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { ConvenioDTO } from '../models/convenio';
import { ConvenioPorVencerDTO } from '../models/convenio-por-vencer';
import { ConvenioResumenEstadoDTO } from '../models/convenio-resumen-estado';
import { ConvenioVencimientoMesDTO } from '../models/convenio-vencimiento-mes';
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

  /** Dashboard: cantidad y presupuesto por estado. 404 con texto si no hay convenios. */
  resumenPorEstado() {
    return this.http.get<ConvenioResumenEstadoDTO[]>(
      `${this.url}/reportes/resumen-por-estado`,
    );
  }
  /** Dashboard: vencimientos de los próximos `meses` meses (incluye meses en 0) */
  vencimientosPorMes(meses = 12) {
    return this.http.get<ConvenioVencimientoMesDTO[]>(
      `${this.url}/reportes/vencimientos-por-mes`,
      { params: { meses } },
    );
  }
}
