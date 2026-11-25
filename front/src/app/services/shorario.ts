import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { HorarioListDTO } from '../models/HorarioListDTO';
import { Observable } from 'rxjs';
import { HorarioInsertDTO } from '../models/HorarioInsertDTO';

@Injectable({
  providedIn: 'root',
})
export class Shorario {
  private url = `${environment.base}/horario`;

  constructor(private http: HttpClient) {}

  getListHorario(): Observable<HorarioListDTO[]> {
    return this.http.get<HorarioListDTO[]>(this.url);
  }

  deleteHorario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  insertHorario(h: HorarioInsertDTO): Observable<HorarioInsertDTO> {
    return this.http.post<HorarioInsertDTO>(this.url, h);
  }

  updateHorario(h: HorarioInsertDTO): Observable<HorarioInsertDTO> {
    return this.http.put<HorarioInsertDTO>(this.url, h);
  }

  IdHorario(id: number): Observable<HorarioListDTO> {
    return this.http.get<HorarioListDTO>(`${this.url}/${id}`);
  }

  getHorarioPersona(idPersona:number):Observable<HorarioListDTO[]>{
    return this.http.get<HorarioListDTO[]>(`${this.url}/individual/${idPersona}`);
  }
}
    
