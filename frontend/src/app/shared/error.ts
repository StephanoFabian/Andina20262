import { HttpErrorResponse } from '@angular/common/http';
export function mensajeError(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0 || error.status === 502 || error.status === 504)
      return 'No se pudo conectar con el servidor. Comprueba que la API esté disponible e inténtalo de nuevo.';
    if (error.status === 401)
      return 'La sesión venció o los datos de acceso son incorrectos.';
    if (error.status === 403)
      return 'Tu rol no tiene permiso para realizar esta acción.';
    if (error.status === 404) return 'No se encontró el registro solicitado.';
    if (error.status === 409)
      return 'Ya existe este registro o tiene datos relacionados. Revisa las dependencias.';
    if (
      typeof error.error === 'string' &&
      error.error.length < 400 &&
      !error.error.includes('<')
    )
      return error.error;
    if (typeof error.error?.message === 'string') return error.error.message;
  }
  return error instanceof Error && !(error instanceof HttpErrorResponse)
    ? error.message
    : 'No se pudo completar la operación. Inténtalo de nuevo.';
}
