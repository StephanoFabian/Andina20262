import { HttpErrorResponse } from '@angular/common/http';
import { WritableSignal } from '@angular/core';
import { Observable } from 'rxjs';
import { mensajeError } from '../../services/errores';

/** Resultado de una consulta: datos (null = aún no consultado) y aviso cuando no hay resultados */
export interface Consulta<T> {
  datos: T[] | null;
  aviso: string | null;
}

export const SIN_CONSULTAR: Consulta<never> = { datos: null, aviso: null };

/**
 * Ejecuta una consulta y guarda el resultado en `destino`.
 * Varios reportes del backend responden 404 con un texto cuando no hay datos:
 * se muestra como aviso, no como error.
 */
export function ejecutarConsulta<T>(
  peticion: Observable<T[]>,
  destino: WritableSignal<Consulta<T>>,
  cargando: WritableSignal<number>,
  error: WritableSignal<string | null>,
  sinDatos = 'No hay datos para mostrar.',
): void {
  cargando.update((n) => n + 1);
  error.set(null);
  peticion.subscribe({
    next: (datos) => {
      destino.set({ datos, aviso: datos.length === 0 ? sinDatos : null });
      cargando.update((n) => n - 1);
    },
    error: (err: unknown) => {
      cargando.update((n) => n - 1);
      if (err instanceof HttpErrorResponse && err.status === 404) {
        destino.set({
          datos: [],
          aviso:
            typeof err.error === 'string' && err.error ? err.error : sinDatos,
        });
      } else {
        destino.set({ datos: null, aviso: null });
        error.set(mensajeError(err));
      }
    },
  });
}
