export interface Matricula {
  idMatricula?: number;
  idColegio: number;
  /** Solo lectura: lo devuelve MatriculaDTOList */
  nombreColegio?: string;
  idPersona: number;
}
