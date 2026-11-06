import { Injectable } from '@angular/core';
import { ConvenioDTO } from '../models/convenio';
import { ConvenioPorVencerDTO } from '../models/convenio-por-vencer';
import { Pagina } from '../models/modulo';
import { EdissonApi } from './edisson-api';
@Injectable({ providedIn: 'root' })
export class Convenioservice extends EdissonApi<ConvenioDTO> {
  constructor() {
    super('/api/convenios', 'idConvenio', '', '/{id}');
  }
  reportePorVencer(dias = 90, page = 0, size = 10) {
    return this.http.get<Pagina<ConvenioPorVencerDTO>>(
      `${this.url}/por-vencer/pagina`,
      { params: { dias, page, size } },
    );
  }
}
