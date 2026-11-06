import { MatPaginatorIntl } from '@angular/material/paginator';
export function paginadorEspanol(): MatPaginatorIntl {
  const p = new MatPaginatorIntl();
  p.itemsPerPageLabel = 'Filas por página';
  p.nextPageLabel = 'Página siguiente';
  p.previousPageLabel = 'Página anterior';
  p.firstPageLabel = 'Primera página';
  p.lastPageLabel = 'Última página';
  p.getRangeLabel = (page, size, total) =>
    total === 0
      ? '0 registros'
      : `${page * size + 1}–${Math.min((page + 1) * size, total)} de ${total}`;
  return p;
}
