import { Routes } from '@angular/router';
import { authGuard, escrituraGuard } from './core/auth.guard';
export const rutasEdisson: Routes = [
  {
    path: 'materiales',
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./components/material/material-listar/material-listar').then(
            (m) => m.MaterialListar,
          ),
      },
      { path: 'listar', redirectTo: '', pathMatch: 'full' },
      {
        path: 'nuevo',
        canActivate: [escrituraGuard],
        data: { modulo: 'materiales' },
        loadComponent: () =>
          import('./components/material/material-insertar/material-insertar').then(
            (m) => m.MaterialInsertar,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'materiales' },
        loadComponent: () =>
          import('./components/material/material-insertar/material-insertar').then(
            (m) => m.MaterialInsertar,
          ),
      },
      {
        path: 'ediciones/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'materiales' },
        loadComponent: () =>
          import('./components/material/material-insertar/material-insertar').then(
            (m) => m.MaterialInsertar,
          ),
      },
      {
        path: 'editar/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'materiales' },
        loadComponent: () =>
          import('./components/material/material-insertar/material-insertar').then(
            (m) => m.MaterialInsertar,
          ),
      },
      {
        path: 'consultas',
        loadComponent: () =>
          import('./components/material/material-consultas/material-consultas').then(
            (m) => m.MaterialConsultas,
          ),
      },
      {
        path: 'reporte-tipo',
        loadComponent: () =>
          import('./components/material/material-reporte-tipo/material-reporte-tipo').then(
            (m) => m.MaterialReporteTipo,
          ),
      },
    ],
  },
  {
    path: 'materiales-cursos',
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./components/materialcurso/materialcurso-listar/materialcurso-listar').then(
            (m) => m.MaterialcursoListar,
          ),
      },
      { path: 'listar', redirectTo: '', pathMatch: 'full' },
      {
        path: 'nuevo',
        canActivate: [escrituraGuard],
        data: { modulo: 'materiales-cursos' },
        loadComponent: () =>
          import('./components/materialcurso/materialcurso-insertar/materialcurso-insertar').then(
            (m) => m.MaterialcursoInsertar,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'materiales-cursos' },
        loadComponent: () =>
          import('./components/materialcurso/materialcurso-insertar/materialcurso-insertar').then(
            (m) => m.MaterialcursoInsertar,
          ),
      },
      {
        path: 'reporte',
        loadComponent: () =>
          import('./components/materialcurso/materialcurso-reporte/materialcurso-reporte').then(
            (m) => m.MaterialcursoReporte,
          ),
      },
    ],
  },
  {
    path: 'convenios',
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./components/convenio/convenio-listar/convenio-listar').then(
            (m) => m.ConvenioListar,
          ),
      },
      { path: 'listar', redirectTo: '', pathMatch: 'full' },
      {
        path: 'nuevo',
        canActivate: [escrituraGuard],
        data: { modulo: 'convenios' },
        loadComponent: () =>
          import('./components/convenio/convenio-insertar/convenio-insertar').then(
            (m) => m.ConvenioInsertar,
          ),
      },
      {
        path: 'insertar',
        canActivate: [escrituraGuard],
        data: { modulo: 'convenios' },
        loadComponent: () =>
          import('./components/convenio/convenio-insertar/convenio-insertar').then(
            (m) => m.ConvenioInsertar,
          ),
      },
      {
        path: 'ediciones/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'convenios' },
        loadComponent: () =>
          import('./components/convenio/convenio-insertar/convenio-insertar').then(
            (m) => m.ConvenioInsertar,
          ),
      },
      {
        path: 'editar/:id',
        canActivate: [escrituraGuard],
        data: { modulo: 'convenios' },
        loadComponent: () =>
          import('./components/convenio/convenio-insertar/convenio-insertar').then(
            (m) => m.ConvenioInsertar,
          ),
      },
      {
        path: 'por-vencer',
        loadComponent: () =>
          import('./components/convenio/convenio-por-vencer/convenio-por-vencer').then(
            (m) => m.ConvenioPorVencer,
          ),
      },
    ],
  },
];
