import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { MaterialCursoDTO } from '../models/materialcurso';
import { CursoMaterialesDTO } from '../models/curso-materiales';
import { Pagina } from '../models/modulo';
@Injectable({ providedIn: 'root' })
export class Materialcursoservice {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/materiales-cursos';
  pagina(page = 0, size = 10, q = '', idMaterial?: number) {
    return this.http.get<Pagina<MaterialCursoDTO>>(`${this.url}/pagina`, {
      params: { page, size, q, ...(idMaterial ? { idMaterial } : {}) },
    });
  }
  insert(dto: MaterialCursoDTO) {
    return this.http.post<MaterialCursoDTO>(`${this.url}/nuevo`, dto);
  }
  delete(idMaterial: number, idCurso: number) {
    return this.http.delete(`${this.url}/${idMaterial}/${idCurso}`, {
      responseType: 'text',
    });
  }
  reporteMaterialesPorCurso(page = 0, size = 10) {
    return this.http.get<Pagina<CursoMaterialesDTO>>(
      `${this.url}/reporte-materiales-por-curso/pagina`,
      { params: { page, size } },
    );
  }
}
