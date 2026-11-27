import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Persona } from '../models/Persona';
import { Registro } from '../models/Registro';
import { environment } from '../../environments/environment';
@Injectable({ providedIn: 'root' })
export class RegistroService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.base + '/registro';
  registrar(dto: Registro) {
    return this.http.post<Persona>(this.url, dto);
  }
}
