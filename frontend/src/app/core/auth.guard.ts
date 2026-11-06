import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { modulo } from '../models/catalogo';
export const authGuard: CanActivateFn = () =>
  inject(AuthService).valid() || inject(Router).createUrlTree(['/login']);
export const escrituraGuard: CanActivateFn = (route) =>
  inject(AuthService).puedeEscribir(modulo(route.data['modulo'])) ||
  inject(Router).createUrlTree(['/inicio']);
