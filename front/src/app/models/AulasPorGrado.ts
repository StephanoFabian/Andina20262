/** Reporte GET /detalles-matricula/reporte-aulas-por-grado/{idPeriodo} (AulasPorGradoDTO) */
export interface AulasPorGrado {
  grado: string;
  nivel: string;
  vigentes: number;
  aulasNecesarias: number;
}
