/** Reporte GET /api/matriculas/reportes/estudiantes-varias-matriculas (EstudianteVariasMatriculasDTO) */
export interface EstudianteVariasMatriculas {
  idPersona: number;
  nombres: string;
  apellidos: string;
  matriculas: number;
  colegios: number;
}
