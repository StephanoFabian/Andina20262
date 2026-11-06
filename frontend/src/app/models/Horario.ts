import { Fila } from './modulo';
export interface Horario extends Fila {
  idHorario?: number;
  idAsignacion: number;
  diaSemana: number;
  horaInicio: string;
  horaFin: string;
}
