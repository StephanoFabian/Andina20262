import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CursoDTOList } from '../models/CursoDTOList';
import { environment } from '../../environments/environment.development';
import { CursoDTOInsert } from '../models/CursoDTOInsert';

@Injectable({
  providedIn: 'root',
})
export class Scurso {

  private url =`${environment.base}/curso`;

  constructor(private http:HttpClient){}

  lista():Observable<CursoDTOList[]>{
    return this.http.get<CursoDTOList[]>(this.url)
  }

  delete(id:number):Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`)

  }

  insertCurso(q: CursoDTOInsert):Observable<CursoDTOInsert>{
    return this.http.post<CursoDTOInsert>(this.url,q)
    
  }

  listid(id:number): Observable<CursoDTOList>{
    return this.http.get<CursoDTOList>(`${this.url}/${id}`);
    }

    updateCurso(dto: CursoDTOInsert): Observable<CursoDTOInsert>{
      return this.http.put<CursoDTOInsert>(this.url, dto);
    }
    
    getArea(a:string){
      return this.http.get<CursoDTOList[]>(
        `${this.url}/area`,
        {params:{ a }});
    }
}
