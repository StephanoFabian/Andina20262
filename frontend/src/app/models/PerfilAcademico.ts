import { Fila } from './modulo';
export interface PerfilAcademico extends Fila {
  idPerfilAcademico?: number;
  detallePA: string;
  notasPA: number;
}
