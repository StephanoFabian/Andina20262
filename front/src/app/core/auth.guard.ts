import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { MODULOS, modulo } from '../models/catalogo';
import { puedeVer } from '../services/permisos';
export const authGuard: CanActivateFn = () =>
  inject(AuthService).valid() || inject(Router).createUrlTree(['/login']);
export const escrituraGuard: CanActivateFn = (route) =>
  inject(AuthService).puedeEscribir(modulo(route.data['modulo'])) ||
  inject(Router).createUrlTree(['/inicio']);
/**
 * Solo deja entrar a un módulo a los roles que lo ven en el menú (permisos.ts).
 * La ruta de cada módulo se llama igual que su clave en el catálogo; las rutas
 * que no son un módulo (inicio) pasan siempre.
 */
export const lecturaGuard: CanActivateFn = (route) => {
  const key = route.routeConfig?.path ?? '';
  if (!MODULOS.some((m) => m.key === key)) return true;
  return (
    puedeVer(key, inject(AuthService).roles()) ||
    inject(Router).createUrlTree(['/inicio'])
  );
};
