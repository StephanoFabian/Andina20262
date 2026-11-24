/** Reporte GET /api/convenios/reportes/vencimientos-por-mes?meses= (ConvenioVencimientoMesDTO) */
export interface ConvenioVencimientoMesDTO {
  /** yyyy-MM */
  mes: string;
  /** Convenios vigentes o en renovación que vencen ese mes */
  cantidad: number;
  presupuesto: number;
}
