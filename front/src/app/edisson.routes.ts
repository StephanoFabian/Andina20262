import { inject } from '@angular/core';
import { CanActivateFn, Router, Routes } from '@angular/router';
import { authGuard, escrituraGuard, lecturaGuard } from './core/auth.guard';
import { AuthService } from './core/auth.service';
import { PERMISOS_EXTRA } from './services/permisos';

/**
 * Los dashboards solo los abren los roles que pueden ver el reporte
 * (PERMISOS_EXTRA en permisos.ts); el resto vuelve al inicio.
 */
const rolesGuard =
  (roles: readonly string[]): CanActivateFn =>
  () => {
    const actuales = inject(AuthService).roles();
    return (
      roles.some((r) => actuales.includes(r)) ||
      inject(Router).createUrlTree(['/inicio'])
    );
  };

export const rutasEdisson: Routes = [
  {
    path: 'materiales',
    canActivate: [authGuard, lecturaGuard],
    children: [
      {
        path: 'crear-con-ia',
        canActivate: [escrituraGuard],
        data: { modulo: 'materiales' },
        loadComponent: () =>
          import('./components/material/material-ia/material-ia').then(
            (m) => m.MaterialIAComponent,
          ),
      },
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
      {
        path: 'dashboard',
        canActivate: [rolesGuard(PERMISOS_EXTRA.materialReporte)],
        loadComponent: () =>
          import('./components/material/material-dashboard/material-dashboard').then(
            (m) => m.MaterialDashboard,
          ),
      },
    ],
  },
  {
    path: 'materiales-cursos',
    canActivate: [authGuard, lecturaGuard],
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
      {
        path: 'dashboard',
        canActivate: [rolesGuard(PERMISOS_EXTRA.materialCursoReporte)],
        loadComponent: () =>
          import('./components/materialcurso/materialcurso-dashboard/materialcurso-dashboard').then(
            (m) => m.MaterialcursoDashboard,
          ),
      },
    ],
  },
  {
    path: 'convenios',
    canActivate: [authGuard, lecturaGuard],
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
