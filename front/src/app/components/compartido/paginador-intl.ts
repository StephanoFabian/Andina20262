import { Injectable } from '@angular/core';
import { MatPaginatorIntl } from '@angular/material/paginator';

/** Textos del paginador en español (listas paginadas en el cliente) */
@Injectable()
export class PaginadorIntl extends MatPaginatorIntl {
  override itemsPerPageLabel = 'Registros por página:';
  override nextPageLabel = 'Página siguiente';
  override previousPageLabel = 'Página anterior';
  override firstPageLabel = 'Primera página';
  override lastPageLabel = 'Última página';

  override getRangeLabel = (
    page: number,
    pageSize: number,
    length: number,
  ): string => {
    if (length === 0 || pageSize === 0) {
      return `0 de ${length}`;
    }
    const inicio = page * pageSize;
    const fin = Math.min(inicio + pageSize, length);
    return `${inicio + 1} – ${fin} de ${length}`;
  };
}

/**
 * Paginación en el servidor: el backend solo indica si hay otra página (X-Has-Next),
 * no el total, así que se muestra el número de página en lugar de "x de N".
 */
@Injectable()
export class PaginadorServidorIntl extends PaginadorIntl {
  override getRangeLabel = (page: number): string => `Página ${page + 1}`;
}
