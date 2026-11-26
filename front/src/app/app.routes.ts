import { Routes } from '@angular/router';
import { rutasEdisson } from './edisson.routes';
import { authGuard, escrituraGuard, lecturaGuard } from './core/auth.guard';
export const routes: Routes = [
  ...rutasEdisson,
  {
    path: 'login',
    loadComponent: () =>
      import('./components/logincomponent/logincomponent').then(
        (m) => m.LoginComponent,
      ),
  },
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () =>
      import('./components/landingcomponent/landingcomponent').then(
        (m) => m.LandingComponent,
      ),
  },
  {
    path: 'registro',
    loadComponent: () =>
      import('./components/registrocomponent/registrocomponent').then(
        (m) => m.RegistroComponent,
      ),
  },
  {
    path: 'inicio',
    canActivate: [authGuard, lecturaGuard],
    loadComponent: () =>
      import('./components/homecomponent/homecomponent').then(
        (m) => m.HomeComponent,
      ),
  },
  {
    path: 'grados',
    canActivate: [authGuard, lecturaGuard],
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
    path: 'periodos-academicos',
    canActivate: [authGuard, lecturaGuard],
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
        path: 'reportes',
        loadComponent: () =>
          import('./components/periodos-academicoscomponent/reportecomponent/reportecomponent').then(
            (m) => m.PeriodoAcademicoReporteComponent,
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
    canActivate: [authGuard, lecturaGuard],
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
        path: 'reportes',
        loadComponent: () =>
          import('./components/matriculascomponent/reportecomponent/reportecomponent').then(
            (m) => m.MatriculaReporteComponent,
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
    canActivate: [authGuard, lecturaGuard],
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
        path: 'reportes',
        loadComponent: () =>
          import('./components/detalles-matriculacomponent/reportecomponent/reportecomponent').then(
            (m) => m.DetalleMatriculaReporteComponent,
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
    path: 'colegios',
    canActivate: [authGuard, lecturaGuard],
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
    canActivate: [authGuard, lecturaGuard],
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
    path: 'sesiones-virtuales',
    canActivate: [authGuard, lecturaGuard],
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
    canActivate: [authGuard, lecturaGuard],
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
  { path: '**', redirectTo: '' },
];
