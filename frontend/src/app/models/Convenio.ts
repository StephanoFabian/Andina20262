import { Fila } from './modulo';
export interface Convenio extends Fila {
  idConvenio?: number;
  nombre: string;
  institucion: string;
  idColegio: number;
  fechaInicio: string;
  fechaFin: string;
  estado: string;
}
