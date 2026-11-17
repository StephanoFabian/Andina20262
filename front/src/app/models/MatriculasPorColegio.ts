/** Reporte GET /api/matriculas/reportes/por-colegio (MatriculasPorColegioDTO) */
export interface MatriculasPorColegio {
  colegio: string;
  departamento: string;
  tipoZona: string;
  totalMatriculas: number;
  porcentajeDelTotal: number | null;
}
