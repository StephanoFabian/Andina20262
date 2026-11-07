import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { timeout } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  AccesoVideollamada,
  FragmentoTraduccion,
} from '../models/Videollamada';

@Injectable({ providedIn: 'root' })
export class VideollamadaService {
  private readonly http = inject(HttpClient);
  private ruta(sesion: number): string {
    return `${environment.base}/api/sesiones-virtuales/${sesion}/videollamada`;
  }
  acceso(sesion: number) {
    return this.http
      .post<AccesoVideollamada>(`${this.ruta(sesion)}/acceso`, {})
      .pipe(timeout(10000));
  }
  enviar(sesion: number, fragmento: FragmentoTraduccion) {
    return this.http
      .post<FragmentoTraduccion>(`${this.ruta(sesion)}/fragmentos`, fragmento)
      .pipe(timeout(10000));
  }
  resultado(sesion: number, id: string) {
    return this.http
      .get<FragmentoTraduccion>(`${this.ruta(sesion)}/fragmentos/${id}`)
      .pipe(timeout(10000));
  }
}
