import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DetalleMatricula } from '../models/DetalleMatricula';
import { RetiroPorCurso } from '../models/RetiroPorCurso';
import { AulasPorGrado } from '../models/AulasPorGrado';
import { RetencionColegio } from '../models/RetencionColegio';
import { Pagina } from '../models/modulo';
import { environment } from '../../environments/environment';
@Injectable({ providedIn: 'root' })
export class DetalleMatriculaService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/detalles-matricula';
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<DetalleMatricula>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<DetalleMatricula>(`${this.url}/${id}`);
  }
  insertar(dto: DetalleMatricula) {
    return this.http.post<DetalleMatricula>(this.url + '/nuevo', dto);
  }
  actualizar(dto: DetalleMatricula) {
    return this.http.put<DetalleMatricula>(
      this.url + '/actualiza'.replace('{id}', String(dto.idDetalleMatricula)),
      dto,
    );
  }
  eliminar(dto: DetalleMatricula) {
    return this.http.delete(`${this.url}/${dto.idDetalleMatricula}`, {
      responseType: 'text',
    });
  }
  reporteRetiroPorCurso() {
    return this.http.get<RetiroPorCurso[]>(
      `${this.url}/reporte-retiro-por-curso`,
    );
  }
  reporteAulasPorGrado(idPeriodo: number) {
    return this.http.get<AulasPorGrado[]>(
      `${this.url}/reporte-aulas-por-grado/${idPeriodo}`,
    );
  }
  reporteRetencion(idPeriodoAnterior: number, idPeriodoActual: number) {
    return this.http.get<RetencionColegio[]>(
      `${this.url}/reporte-retencion-colegio/${idPeriodoAnterior}/${idPeriodoActual}`,
    );
  }
}
