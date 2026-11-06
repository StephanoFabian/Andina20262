import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { Pagina } from '../models/modulo';
export class EdissonApi<T> {
  protected readonly http = inject(HttpClient);
  protected readonly url: string;
  constructor(
    path: string,
    private readonly idKey: string,
    private readonly nuevo = '/nuevo',
    private readonly actualiza = '/actualiza',
  ) {
    this.url = environment.base + path;
  }
  pagina(page = 0, size = 10, q = '') {
    return this.http.get<Pagina<T>>(`${this.url}/pagina`, {
      params: { page, size, q },
    });
  }
  listId(id: number) {
    return this.http.get<T>(`${this.url}/${id}`);
  }
  insert(dto: T) {
    return this.http.post<T>(this.url + this.nuevo, dto);
  }
  update(dto: T) {
    const id = (dto as Record<string, unknown>)[this.idKey];
    return this.http.put<T>(
      this.url + this.actualiza.replace('{id}', String(id)),
      dto,
    );
  }
  delete(id: number) {
    return this.http.delete(`${this.url}/${id}`, { responseType: 'text' });
  }
}
