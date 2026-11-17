/** Reporte GET /api/periodos-academicos/reportes/matricula-tardia (MatriculaTardiaDTO) */
export interface MatriculaTardia {
  periodo: string;
  /** yyyy-MM-dd */
  fechaInicio: string;
  totalMatriculas: number;
  anticipadas: number;
  tardias: number;
  fueraDelPeriodo: number;
  porcentajeTardias: number | null;
}
