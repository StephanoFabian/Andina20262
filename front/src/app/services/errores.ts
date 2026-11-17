import { HttpErrorResponse } from '@angular/common/http';

/**
 * Convierte un error HTTP del backend en un mensaje legible en español.
 * El backend responde con texto plano o con ErrorResponse { status, message, timestamp }.
 */
export function mensajeError(err: unknown): string {
  if (!(err instanceof HttpErrorResponse)) {
    return 'Ocurrió un error inesperado.';
  }
  if (err.status === 0) {
    return 'No se pudo conectar con el servidor. Verifica que el backend esté en ejecución.';
  }
  const detalle = extraerDetalle(err.error);
  switch (err.status) {
    case 401:
      return 'Tu sesión expiró o no es válida. Vuelve a iniciar sesión.';
    case 403:
      return detalle ?? 'No tienes permisos para realizar esta acción.';
    case 404:
      return detalle ?? 'El registro solicitado no existe.';
    default:
      return detalle ?? `El servidor respondió con un error (${err.status}).`;
  }
}

function extraerDetalle(body: unknown): string | null {
  if (typeof body === 'string' && body.trim() !== '') {
    return body;
  }
  if (body && typeof body === 'object' && 'message' in body) {
    const message = (body as { message: unknown }).message;
    if (typeof message === 'string' && message.trim() !== '') {
      return message;
    }
  }
  return null;
}
