/** Reporte GET /detalles-matricula/reporte-retencion-colegio/{idPeriodoAnterior}/{idPeriodoActual} (RetencionColegioDTO) */
export interface RetencionColegio {
  colegio: string;
  alumnosAnterior: number;
  alumnosActual: number;
  siguieron: number;
  seFueron: number;
  nuevos: number;
  variacion: number;
}
