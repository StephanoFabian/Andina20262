import { inject } from '@angular/core';
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService),
    router = inject(Router);
  const isApi = req.url.startsWith(environment.base + '/');
  const token = isApi ? auth.token() : null;
  return next(
    token
      ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : req,
  ).pipe(
    catchError((err: HttpErrorResponse) => {
      if (isApi && err.status === 401 && !req.url.endsWith('/login')) {
        auth.logout();
        void router.navigate(['/login']);
      }
      return throwError(() => err);
    }),
  );
};
