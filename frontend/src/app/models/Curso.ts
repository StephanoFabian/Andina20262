import { Fila } from './modulo';
export interface Curso extends Fila {
  id_curso?: number;
  nombre_curso: string;
  descripcion: string;
  area: string;
}
