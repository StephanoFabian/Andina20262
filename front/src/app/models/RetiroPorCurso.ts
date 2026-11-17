/** Reporte GET /detalles-matricula/reporte-retiro-por-curso (RetiroPorCursoDTO) */
export interface RetiroPorCurso {
  curso: string;
  area: string;
  totalMatriculados: number;
  retirados: number;
  trasladados: number;
  porcentajePerdida: number | null;
}
