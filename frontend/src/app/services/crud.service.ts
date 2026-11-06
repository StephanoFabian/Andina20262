import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Fila, Modulo, Pagina } from '../models/modulo';
export class CrudService<T extends Fila> {
  constructor(
    protected http: HttpClient,
    public config: Modulo,
  ) {}
  pagina(page = 0, size = 10, q = ''): Observable<Pagina<T>> {
    return this.http.get<Pagina<T>>(
      `${environment.base}${this.config.path}/pagina`,
      {
        params: new HttpParams()
          .set('page', page)
          .set('size', size)
          .set('q', q),
      },
    );
  }
  listId(id: number): Observable<T> {
    return this.http.get<T>(`${environment.base}${this.config.path}/${id}`);
  }
  insertar(dto: T): Observable<T> {
    return this.http.post<T>(
      `${environment.base}${this.config.path}${this.config.createPath}`,
      dto,
    );
  }
  actualizar(dto: T): Observable<T> {
    return this.http.put<T>(
      `${environment.base}${this.config.path}${this.config.updatePath.replace('{id}', String(dto[this.config.id]))}`,
      dto,
    );
  }
  eliminar(dto: T): Observable<string> {
    const path = this.config.deletePath.replace(
      /\{([^}]+)\}/g,
      (_, key: string) => String(dto[key === 'id' ? this.config.id : key]),
    );
    return this.http.delete(`${environment.base}${this.config.path}${path}`, {
      responseType: 'text',
    });
  }
}
