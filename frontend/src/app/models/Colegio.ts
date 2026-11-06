import { Fila } from './modulo';
export interface Colegio extends Fila {
  idColegio?: number;
  nombre: string;
  departamento: string;
  provincia: string;
  distrito: string;
  comunidad: string;
  tipo_zona: string;
}
