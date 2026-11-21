/** Reporte GET /api/periodos-academicos/reportes/evolucion-matricula (EvolucionPeriodoDTO) */
export interface EvolucionPeriodo {
  periodo: string;
  /** yyyy-MM-dd */
  fechaInicio: string;
  estado: string;
  matriculados: number;
  /** Matriculados del periodo anterior; null en el primero */
  periodoAnterior: number | null;
  variacionPorcentual: number | null;
}
