import { Routes } from '@angular/router';
import { rutasEdisson } from './edisson.routes';
import { authGuard, escrituraGuard } from './core/auth.guard';
export const routes: Routes = [
  ...rutasEdisson,
  {
    path: 'login',
    loadComponent: () =>
      import('./components/logincomponent/logincomponent').then(
        (m) => m.LoginComponent,
      ),
  },
  { path: '', pathMatch: 'full', redirectTo: 'inicio' },
  {
    path: 'inicio',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/homecomponent/homecomponent').then(
        (m) => m.HomeComponent,
      ),
  },
  {
    path: 'colegios',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/colegioscomponent/colegioscomponent').then(
        (m) => m.ColegioComponent,
      ),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'listar' },
      {
        path: 'listar',
        loadComponent: () =>
          import('./components/colegioscomponent/listarcomponent/listarcomponent').then(
            (m) => m.ColegioListarComponent,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'colegios' },
        loadComponent: () =>
          import('./components/colegioscomponent/insertarcomponent/insertarcomponent').then(
            (m) => m.ColegioInsertarComponent,
          ),
      },
      {
        path: 'editar/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'colegios' },
        loadComponent: () =>
          import('./components/colegioscomponent/actualizarcomponent/actualizarcomponent').then(
            (m) => m.ColegioActualizarComponent,
          ),
      },
    ],
  },
  {
    path: 'aulas',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/aulascomponent/aulascomponent').then(
        (m) => m.AulaComponent,
      ),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'listar' },
      {
        path: 'listar',
        loadComponent: () =>
          import('./components/aulascomponent/listarcomponent/listarcomponent').then(
            (m) => m.AulaListarComponent,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'aulas' },
        loadComponent: () =>
          import('./components/aulascomponent/insertarcomponent/insertarcomponent').then(
            (m) => m.AulaInsertarComponent,
          ),
      },
      {
        path: 'editar/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'aulas' },
        loadComponent: () =>
          import('./components/aulascomponent/actualizarcomponent/actualizarcomponent').then(
            (m) => m.AulaActualizarComponent,
          ),
      },
    ],
  },
  {
    path: 'grados',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/gradoscomponent/gradoscomponent').then(
        (m) => m.GradoComponent,
      ),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'listar' },
      {
        path: 'listar',
        loadComponent: () =>
          import('./components/gradoscomponent/listarcomponent/listarcomponent').then(
            (m) => m.GradoListarComponent,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'grados' },
        loadComponent: () =>
          import('./components/gradoscomponent/insertarcomponent/insertarcomponent').then(
            (m) => m.GradoInsertarComponent,
          ),
      },
      {
        path: 'editar/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'grados' },
        loadComponent: () =>
          import('./components/gradoscomponent/actualizarcomponent/actualizarcomponent').then(
            (m) => m.GradoActualizarComponent,
          ),
      },
    ],
  },
  {
    path: 'cursos',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/cursoscomponent/cursoscomponent').then(
        (m) => m.CursoComponent,
      ),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'listar' },
      {
        path: 'listar',
        loadComponent: () =>
          import('./components/cursoscomponent/listarcomponent/listarcomponent').then(
            (m) => m.CursoListarComponent,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'cursos' },
        loadComponent: () =>
          import('./components/cursoscomponent/insertarcomponent/insertarcomponent').then(
            (m) => m.CursoInsertarComponent,
          ),
      },
      {
        path: 'editar/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'cursos' },
        loadComponent: () =>
          import('./components/cursoscomponent/actualizarcomponent/actualizarcomponent').then(
            (m) => m.CursoActualizarComponent,
          ),
      },
    ],
  },
  {
    path: 'periodos-academicos',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/periodos-academicoscomponent/periodos-academicoscomponent').then(
        (m) => m.PeriodoAcademicoComponent,
      ),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'listar' },
      {
        path: 'listar',
        loadComponent: () =>
          import('./components/periodos-academicoscomponent/listarcomponent/listarcomponent').then(
            (m) => m.PeriodoAcademicoListarComponent,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'periodos-academicos' },
        loadComponent: () =>
          import('./components/periodos-academicoscomponent/insertarcomponent/insertarcomponent').then(
            (m) => m.PeriodoAcademicoInsertarComponent,
          ),
      },
      {
        path: 'editar/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'periodos-academicos' },
        loadComponent: () =>
          import('./components/periodos-academicoscomponent/actualizarcomponent/actualizarcomponent').then(
            (m) => m.PeriodoAcademicoActualizarComponent,
          ),
      },
    ],
  },
  {
    path: 'matriculas',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/matriculascomponent/matriculascomponent').then(
        (m) => m.MatriculaComponent,
      ),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'listar' },
      {
        path: 'listar',
        loadComponent: () =>
          import('./components/matriculascomponent/listarcomponent/listarcomponent').then(
            (m) => m.MatriculaListarComponent,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'matriculas' },
        loadComponent: () =>
          import('./components/matriculascomponent/insertarcomponent/insertarcomponent').then(
            (m) => m.MatriculaInsertarComponent,
          ),
      },
      {
        path: 'editar/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'matriculas' },
        loadComponent: () =>
          import('./components/matriculascomponent/actualizarcomponent/actualizarcomponent').then(
            (m) => m.MatriculaActualizarComponent,
          ),
      },
    ],
  },
  {
    path: 'detalles-matricula',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/detalles-matriculacomponent/detalles-matriculacomponent').then(
        (m) => m.DetalleMatriculaComponent,
      ),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'listar' },
      {
        path: 'listar',
        loadComponent: () =>
          import('./components/detalles-matriculacomponent/listarcomponent/listarcomponent').then(
            (m) => m.DetalleMatriculaListarComponent,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'detalles-matricula' },
        loadComponent: () =>
          import('./components/detalles-matriculacomponent/insertarcomponent/insertarcomponent').then(
            (m) => m.DetalleMatriculaInsertarComponent,
          ),
      },
      {
        path: 'editar/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'detalles-matricula' },
        loadComponent: () =>
          import('./components/detalles-matriculacomponent/actualizarcomponent/actualizarcomponent').then(
            (m) => m.DetalleMatriculaActualizarComponent,
          ),
      },
    ],
  },
  {
    path: 'personas',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/personascomponent/personascomponent').then(
        (m) => m.PersonaComponent,
      ),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'listar' },
      {
        path: 'listar',
        loadComponent: () =>
          import('./components/personascomponent/listarcomponent/listarcomponent').then(
            (m) => m.PersonaListarComponent,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'personas' },
        loadComponent: () =>
          import('./components/personascomponent/insertarcomponent/insertarcomponent').then(
            (m) => m.PersonaInsertarComponent,
          ),
      },
      {
        path: 'editar/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'personas' },
        loadComponent: () =>
          import('./components/personascomponent/actualizarcomponent/actualizarcomponent').then(
            (m) => m.PersonaActualizarComponent,
          ),
      },
    ],
  },
  {
    path: 'roles',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/rolescomponent/rolescomponent').then(
        (m) => m.RolComponent,
      ),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'listar' },
      {
        path: 'listar',
        loadComponent: () =>
          import('./components/rolescomponent/listarcomponent/listarcomponent').then(
            (m) => m.RolListarComponent,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'roles' },
        loadComponent: () =>
          import('./components/rolescomponent/insertarcomponent/insertarcomponent').then(
            (m) => m.RolInsertarComponent,
          ),
      },
      {
        path: 'editar/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'roles' },
        loadComponent: () =>
          import('./components/rolescomponent/actualizarcomponent/actualizarcomponent').then(
            (m) => m.RolActualizarComponent,
          ),
      },
    ],
  },
  {
    path: 'perfiles-academicos',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/perfiles-academicoscomponent/perfiles-academicoscomponent').then(
        (m) => m.PerfilAcademicoComponent,
      ),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'listar' },
      {
        path: 'listar',
        loadComponent: () =>
          import('./components/perfiles-academicoscomponent/listarcomponent/listarcomponent').then(
            (m) => m.PerfilAcademicoListarComponent,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'perfiles-academicos' },
        loadComponent: () =>
          import('./components/perfiles-academicoscomponent/insertarcomponent/insertarcomponent').then(
            (m) => m.PerfilAcademicoInsertarComponent,
          ),
      },
      {
        path: 'editar/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'perfiles-academicos' },
        loadComponent: () =>
          import('./components/perfiles-academicoscomponent/actualizarcomponent/actualizarcomponent').then(
            (m) => m.PerfilAcademicoActualizarComponent,
          ),
      },
    ],
  },
  {
    path: 'asignaciones',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/asignacionescomponent/asignacionescomponent').then(
        (m) => m.AsignacionDocenteComponent,
      ),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'listar' },
      {
        path: 'listar',
        loadComponent: () =>
          import('./components/asignacionescomponent/listarcomponent/listarcomponent').then(
            (m) => m.AsignacionDocenteListarComponent,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'asignaciones' },
        loadComponent: () =>
          import('./components/asignacionescomponent/insertarcomponent/insertarcomponent').then(
            (m) => m.AsignacionDocenteInsertarComponent,
          ),
      },
      {
        path: 'editar/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'asignaciones' },
        loadComponent: () =>
          import('./components/asignacionescomponent/actualizarcomponent/actualizarcomponent').then(
            (m) => m.AsignacionDocenteActualizarComponent,
          ),
      },
    ],
  },
  {
    path: 'sesiones-virtuales',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/sesiones-virtualescomponent/sesiones-virtualescomponent').then(
        (m) => m.SesionVirtualComponent,
      ),
    children: [
      {
        path: 'sala/:id',
        loadComponent: () =>
          import('./components/sesiones-virtualescomponent/videollamadacomponent/videollamadacomponent').then(
            (m) => m.VideollamadaComponent,
          ),
      },
      { path: '', pathMatch: 'full', redirectTo: 'listar' },
      {
        path: 'listar',
        loadComponent: () =>
          import('./components/sesiones-virtualescomponent/listarcomponent/listarcomponent').then(
            (m) => m.SesionVirtualListarComponent,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'sesiones-virtuales' },
        loadComponent: () =>
          import('./components/sesiones-virtualescomponent/insertarcomponent/insertarcomponent').then(
            (m) => m.SesionVirtualInsertarComponent,
          ),
      },
      {
        path: 'editar/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'sesiones-virtuales' },
        loadComponent: () =>
          import('./components/sesiones-virtualescomponent/actualizarcomponent/actualizarcomponent').then(
            (m) => m.SesionVirtualActualizarComponent,
          ),
      },
    ],
  },
  {
    path: 'asistencias',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/asistenciascomponent/asistenciascomponent').then(
        (m) => m.AsistenciaComponent,
      ),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'listar' },
      {
        path: 'listar',
        loadComponent: () =>
          import('./components/asistenciascomponent/listarcomponent/listarcomponent').then(
            (m) => m.AsistenciaListarComponent,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'asistencias' },
        loadComponent: () =>
          import('./components/asistenciascomponent/insertarcomponent/insertarcomponent').then(
            (m) => m.AsistenciaInsertarComponent,
          ),
      },
      {
        path: 'editar/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'asistencias' },
        loadComponent: () =>
          import('./components/asistenciascomponent/actualizarcomponent/actualizarcomponent').then(
            (m) => m.AsistenciaActualizarComponent,
          ),
      },
    ],
  },
  {
    path: 'horarios',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/horarioscomponent/horarioscomponent').then(
        (m) => m.HorarioComponent,
      ),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'listar' },
      {
        path: 'listar',
        loadComponent: () =>
          import('./components/horarioscomponent/listarcomponent/listarcomponent').then(
            (m) => m.HorarioListarComponent,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'horarios' },
        loadComponent: () =>
          import('./components/horarioscomponent/insertarcomponent/insertarcomponent').then(
            (m) => m.HorarioInsertarComponent,
          ),
      },
      {
        path: 'editar/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'horarios' },
        loadComponent: () =>
          import('./components/horarioscomponent/actualizarcomponent/actualizarcomponent').then(
            (m) => m.HorarioActualizarComponent,
          ),
      },
    ],
  },
  {
    path: 'incidencias',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/incidenciascomponent/incidenciascomponent').then(
        (m) => m.IncidenciaComponent,
      ),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'listar' },
      {
        path: 'listar',
        loadComponent: () =>
          import('./components/incidenciascomponent/listarcomponent/listarcomponent').then(
            (m) => m.IncidenciaListarComponent,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'incidencias' },
        loadComponent: () =>
          import('./components/incidenciascomponent/insertarcomponent/insertarcomponent').then(
            (m) => m.IncidenciaInsertarComponent,
          ),
      },
      {
        path: 'editar/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'incidencias' },
        loadComponent: () =>
          import('./components/incidenciascomponent/actualizarcomponent/actualizarcomponent').then(
            (m) => m.IncidenciaActualizarComponent,
          ),
      },
    ],
  },
  { path: '**', redirectTo: 'inicio' },
];
