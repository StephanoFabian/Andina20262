import { Fila } from './modulo';
export interface Rol extends Fila {
  idTipoPersona?: number;
  detalle: string;
}
