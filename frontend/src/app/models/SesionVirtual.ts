import { Fila } from './modulo';
export interface SesionVirtual extends Fila {
  idSesionVirtual?: number;
  titulo: string;
  idAsignacion: number;
  fecha: string;
  horaInicio: string;
  horaFin: string;
  enlace: string;
  estado: string;
}
