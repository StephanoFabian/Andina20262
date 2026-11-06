import { Fila } from './modulo';
export interface DetalleMatricula extends Fila {
  idDetalleMatricula?: number;
  fechaMatricula: string;
  estado: string;
  idMatricula: number;
  idCurso: number;
  idPeriodo: number;
  idGrado: number;
}
