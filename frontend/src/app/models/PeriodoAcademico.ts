import { Fila } from './modulo';
export interface PeriodoAcademico extends Fila {
  idPeriodo?: number;
  nombre: string;
  fechaInicio: string;
  fechaFin: string;
  estado: string;
}
