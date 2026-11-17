import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { timeout } from 'rxjs';
import { environment } from '../../environments/environment';
import { MaterialIA } from '../models/MaterialIA';

@Injectable({ providedIn: 'root' })
export class MaterialIAService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/api/materiales-ia';
  generar(dto: MaterialIA) {
    return this.http.post<MaterialIA>(this.url, dto).pipe(timeout(12000));
  }
  consultar(id: string) {
    return this.http.get<MaterialIA>(`${this.url}/${id}`).pipe(timeout(12000));
  }
  archivo(id: string, nombre: string) {
    return this.http
      .get(`${this.url}/${id}/archivos/${nombre}`, { responseType: 'blob' })
      .pipe(timeout(15000));
  }
}
