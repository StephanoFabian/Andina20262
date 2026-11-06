#!/usr/bin/env python3
"""Genera modelos, servicios y componentes a partir de frontend/modulos.json.
No modifica core/, shared/, login ni home. Ejecutar desde cualquier directorio.
"""
from pathlib import Path
import json
root=Path(__file__).resolve().parents[1]/'frontend'
app=root/'src/app'
def put(path,text):
 p=app/path;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(text)
mods=json.loads((root/'modulos.json').read_text())
put(Path('models/modulo.ts'),'''export type Fila = Record<string, string | number | null>;
export interface Pagina<T> { content: T[]; totalElements: number; number: number; size: number; }
export interface Campo { key: string; label: string; type: string; required: boolean; resource?: string; options?: (string | {value: number; label: string})[]; min?: number; max?: number; minLength?: number; maxLength?: number; }
export interface Modulo { key: string; name: string; label: string; owner: string; path: string; id: string; fields: Campo[]; createPath: string; updatePath: string; deletePath: string; roles: string[]; editable: boolean; source?: string; }
''')
put(Path('models/catalogo.ts'),"import { Modulo } from './modulo';\nexport const MODULOS: Modulo[] = "+json.dumps(mods,ensure_ascii=False,indent=2)+";\nexport const modulo = (key: string): Modulo => { const found=MODULOS.find(m=>m.key===key); if (!found) throw new Error('Módulo desconocido'); return found; };\n")
put(Path('services/crud.service.ts'),'''import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Fila, Modulo, Pagina } from '../models/modulo';
export class CrudService<T extends Fila> {
  constructor(protected http: HttpClient, public config: Modulo) {}
  pagina(page=0,size=10,q=''): Observable<Pagina<T>> { return this.http.get<Pagina<T>>(`${environment.base}${this.config.path}/pagina`,{params:new HttpParams().set('page',page).set('size',size).set('q',q)}); }
  listId(id: number): Observable<T> { return this.http.get<T>(`${environment.base}${this.config.path}/${id}`); }
  insertar(dto: T): Observable<T> { return this.http.post<T>(`${environment.base}${this.config.path}${this.config.createPath}`,dto); }
  actualizar(dto: T): Observable<T> { return this.http.put<T>(`${environment.base}${this.config.path}${this.config.updatePath.replace('{id}',String(dto[this.config.id]))}`,dto); }
  eliminar(dto: T): Observable<string> { const path=this.config.deletePath.replace(/\\{([^}]+)\\}/g,(_,key:string)=>String(dto[key==='id'?this.config.id:key]));return this.http.delete(`${environment.base}${this.config.path}${path}`,{responseType:'text'}); }
}
''')
listtemplate='''<section class="page">
  <div class="page-heading"><div><p class="eyebrow">GESTIÓN EDUCATIVA</p><h1>{{ config.label }}</h1><p>Consulta y administra los registros de {{ config.label.toLowerCase() }}.</p></div>
  @if (puedeEscribir()) {<a mat-flat-button [routerLink]="['/',config.key,'insertar']">+ Nuevo registro</a>}</div>
  <div class="panel"><div class="table-tools"><mat-form-field appearance="outline"><mat-label>Buscar por prefijo</mat-label><input matInput [formControl]="busqueda" maxlength="100" placeholder="Escribe el comienzo del nombre o estado"><mat-icon matPrefix>search</mat-icon></mat-form-field><button mat-stroked-button (click)="cargar()" [disabled]="cargando()">Actualizar</button></div>
  @if (error()) {<div class="notice error" role="alert">{{ error() }} <button mat-button (click)="cargar()">Reintentar</button></div>}
  @if (cargando()) {<mat-progress-bar mode="indeterminate" aria-label="Cargando registros"/>}
  <div class="table-scroll"><table mat-table [dataSource]="filas()">
  @for (col of columnas; track col.key) {<ng-container [matColumnDef]="col.key"><th mat-header-cell *matHeaderCellDef>{{ col.label }}</th><td mat-cell *matCellDef="let row">{{ mostrar(row[col.key],col.key) }}</td></ng-container>}
  <ng-container matColumnDef="acciones"><th mat-header-cell *matHeaderCellDef>Acciones</th><td mat-cell *matCellDef="let row"><div class="row-actions">@if (puedeEscribir()) {@if(config.editable){<a mat-icon-button [routerLink]="['/',config.key,'editar',row[config.id]]" aria-label="Editar registro"><mat-icon>edit</mat-icon></a>}<button mat-icon-button (click)="eliminar(row)" [disabled]="ocupado()" aria-label="Eliminar registro"><mat-icon>delete_outline</mat-icon></button>}@else {<span class="muted">Solo lectura</span>}</div></td></ng-container>
  <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr><tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
  </table></div>
  @if (!cargando() && !error() && filas().length===0) {<div class="empty"><mat-icon>inbox</mat-icon><h2>No hay registros</h2><p>{{ busqueda.value ? 'Prueba con otro término de búsqueda.' : 'Los registros aparecerán aquí cuando los agregues.' }}</p></div>}
  <mat-paginator [length]="total()" [pageIndex]="page" [pageSize]="size" [pageSizeOptions]="[10,25,50,100]" (page)="paginar($event)" aria-label="Paginación de registros"/>
  </div>
</section>
'''
formtemplate='''<section class="page narrow"><a class="back-link" [routerLink]="['/',config.key]">← Volver a {{ config.label.toLowerCase() }}</a><p class="eyebrow">{{ editando ? 'EDITAR REGISTRO' : 'NUEVO REGISTRO' }}</p><h1>{{ config.label }}</h1><p>Los campos marcados con * son obligatorios.</p>
<div class="panel form-panel">
@if(cargando()){<mat-progress-bar mode="indeterminate" aria-label="Cargando registro"/>}
@if(error()){<div class="notice error" role="alert">{{error()}}</div>}
<form [formGroup]="form" (ngSubmit)="guardar()" class="form-grid">
@for(campo of config.fields;track campo.key){
  @if(campo.type==='reference'){<app-referencia [control]="control(campo.key)" [resource]="campo.resource!" [label]="campo.label"/>}
  @else {<mat-form-field appearance="outline" [class.full-width]="campo.type==='textarea'"><mat-label>{{campo.label}}</mat-label>
    @if(campo.type==='select'){<mat-select [formControlName]="campo.key">@for(op of campo.options;track opcionValor(op)){<mat-option [value]="opcionValor(op)">{{opcionEtiqueta(op)}}</mat-option>}</mat-select>}
    @else if(campo.type==='textarea'){<textarea matInput [formControlName]="campo.key" rows="3" [attr.maxlength]="campo.maxLength || null"></textarea>}
    @else {<input matInput [type]="campo.type" [formControlName]="campo.key" [attr.maxlength]="campo.maxLength || null" [attr.min]="campo.min ?? null" [attr.max]="campo.max ?? null" [attr.step]="campo.type==='number' ? 'any' : null" [attr.autocomplete]="campo.type==='password' ? 'new-password' : 'off'">}
    @if(campo.type==='password' && editando){<mat-hint>Déjala vacía para conservar la contraseña actual.</mat-hint>}
    <mat-error>{{errorCampo(campo)}}</mat-error>
  </mat-form-field>}
}
@if(form.hasError('rango')){<p class="notice error full-width" role="alert">La fecha u hora final debe ser posterior a la inicial.</p>}
<div class="form-actions full-width"><button mat-flat-button type="submit" [disabled]="guardando() || cargando() || cargaFallida()">{{guardando() ? 'Guardando…' : 'Guardar registro'}}</button><a mat-stroked-button [routerLink]="['/',config.key]">Cancelar</a></div>
</form></div></section>
'''
routes=[]
for m in mods:
 if m.get('source')=='edisson':continue
 key,name=m['key'],m['name'];tsfields=[f"  {m['id']}?: number;"]
 for f in m['fields']:
  if f['key']==m['id']:continue
  typ='number' if f['type'] in ['number','reference'] or f['key']=='diaSemana' else 'string'
  tsfields.append(f"  {f['key']}{'?' if not f['required'] else ''}: {typ};")
 put(Path('models')/(name+'.ts'),"import { Fila } from './modulo';\nexport interface "+name+" extends Fila {\n"+'\n'.join(tsfields)+"\n}\n".replace('',''))
 # Optional fields need undefined in Fila's index signature (corrected below).
 service=f'''import {{ Injectable }} from '@angular/core';
import {{ HttpClient }} from '@angular/common/http';
import {{ {name} }} from '../models/{name}';
import {{ modulo }} from '../models/catalogo';
import {{ CrudService }} from './crud.service';
@Injectable({{providedIn:'root'}})
export class {name}Service extends CrudService<{name}> {{
  constructor(http: HttpClient) {{ super(http,modulo('{key}')); }}
}}
'''
 put(Path('services')/(key+'.service.ts'),service)
 compdir=Path('components')/(key+'component')
 put(compdir/(key+'component.ts'),f'''import {{ Component }} from '@angular/core';
import {{ RouterOutlet }} from '@angular/router';
@Component({{selector:'app-{key}',imports:[RouterOutlet],template:'<router-outlet />'}})
export class {name}Component {{}}
''')
 for action,base in [('listar','ListadoBase'),('insertar','FormularioBase'),('actualizar','FormularioBase')]:
  directory=compdir/(action+'component');cls=name+action.capitalize()+'Component'
  put(directory/(action+'component.ts'),f'''import {{ Component }} from '@angular/core';
import {{ {base}, {'LISTADO_IMPORTS' if action=='listar' else 'FORMULARIO_IMPORTS'} }} from '../../../shared/{'listado.base' if action=='listar' else 'formulario.base'}';
import {{ {name}Service }} from '../../../services/{key}.service';
import {{ {name} }} from '../../../models/{name}';
@Component({{selector:'app-{key}-{action}',imports:{'LISTADO_IMPORTS' if action=='listar' else 'FORMULARIO_IMPORTS'},templateUrl:'./{action}component.html'}})
export class {cls} extends {base}<{name}> {{ constructor(service: {name}Service) {{ super(service); }} }}
''')
  put(directory/(action+'component.html'),listtemplate if action=='listar' else formtemplate)
 routes.append(f'''  {{path:'{key}',canActivate:[authGuard],loadComponent:()=>import('./components/{key}component/{key}component').then(m=>m.{name}Component),children:[
    {{path:'',pathMatch:'full',redirectTo:'listar'}},
    {{path:'listar',loadComponent:()=>import('./components/{key}component/listarcomponent/listarcomponent').then(m=>m.{name}ListarComponent)}},
    {{path:'insertar',canActivate:[escrituraGuard],data:{{modulo:'{key}'}},loadComponent:()=>import('./components/{key}component/insertarcomponent/insertarcomponent').then(m=>m.{name}InsertarComponent)}},
''' + (f"    {{path:'editar/:id',canActivate:[escrituraGuard],data:{{modulo:'{key}'}},loadComponent:()=>import('./components/{key}component/actualizarcomponent/actualizarcomponent').then(m=>m.{name}ActualizarComponent)}},\n" if m['editable'] else '')+'  ]},')
put(Path('app.routes.ts'),'''import { Routes } from '@angular/router';
import { rutasEdisson } from './edisson.routes';
import { authGuard, escrituraGuard } from './core/auth.guard';
export const routes: Routes = [
  ...rutasEdisson,
  {path:'login',loadComponent:()=>import('./components/logincomponent/logincomponent').then(m=>m.LoginComponent)},
  {path:'',pathMatch:'full',redirectTo:'inicio'},
  {path:'inicio',canActivate:[authGuard],loadComponent:()=>import('./components/homecomponent/homecomponent').then(m=>m.HomeComponent)},
'''+ '\n'.join(routes)+'''\n  {path:'**',redirectTo:'inicio'}
];
''')
p=app/'models/modulo.ts';p.write_text(p.read_text().replace('string | number | null>','string | number | null | undefined>'))
print('Frontend generado. Ejecuta npm run format en frontend/.')
