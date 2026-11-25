import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AsignacionDTOList } from '../models/AsignacionDTOList';
import { AsignacionDTOInsert } from '../models/AsignacionDTOInsert';

@Injectable({
  providedIn: 'root',
})
export class Sasignacion {
  private url = `${environment.base}/asignacion`;

  constructor(private http: HttpClient) {}

  getListAsignacion(): Observable<AsignacionDTOList[]> {
    return this.http.get<AsignacionDTOList[]>(this.url);
  }

  deleteAsignacion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  insertAsignacion(ag: AsignacionDTOInsert): Observable<AsignacionDTOInsert> {
    return this.http.post<AsignacionDTOInsert>(this.url, ag);
  }

  IdAsignacion(id: number): Observable<AsignacionDTOList> {
    return this.http.get<AsignacionDTOList>(`${this.url}/${id}`);
  }

  updateAsignacion(ad: AsignacionDTOInsert): Observable<AsignacionDTOInsert> {
    return this.http.put<AsignacionDTOInsert>(this.url, ad);
  }

  GetRangoHoras(min:number, max:number){
    return this.http.get<AsignacionDTOList[]>(`${this.url}/horarios`, {params:{min, max}});
  }
}
