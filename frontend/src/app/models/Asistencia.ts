import { Fila } from './modulo';
export interface Asistencia extends Fila {
  idAsistencia?: number;
  idSesionVirtual: number;
  idPersona: number;
  estado: string;
  observacion?: string;
}
