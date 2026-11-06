import { Fila } from './modulo';
export interface Grado extends Fila {
  idGrado?: number;
  nombre: string;
  nivel: string;
}
