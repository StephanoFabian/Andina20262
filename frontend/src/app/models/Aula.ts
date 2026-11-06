import { Fila } from './modulo';
export interface Aula extends Fila {
  idAula?: number;
  nombre: string;
  seccion: string;
  capacidad: number;
  idColegio: number;
}
