import { Fila } from './modulo';
export interface Incidencia extends Fila {
  idIncidencia?: number;
  titulo: string;
  descripcion: string;
  fecha: string;
  idPersona: number;
  idColegio: number;
  estado: string;
  prioridad: string;
}
