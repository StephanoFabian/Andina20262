/** Reporte GET /api/convenios/reportes/resumen-por-estado (ConvenioResumenEstadoDTO) */
export interface ConvenioResumenEstadoDTO {
  /** VIGENTE, EN_RENOVACION, VENCIDO, CANCELADO o FINALIZADO */
  estado: string;
  cantidad: number;
  presupuesto: number;
}
