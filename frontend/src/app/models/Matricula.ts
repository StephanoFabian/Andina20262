import { Fila } from './modulo';
export interface Matricula extends Fila {
  idMatricula?: number;
  idColegio: number;
  idPersona: number;
}
