import { Fila } from './modulo';
export interface Persona extends Fila {
  idPersona?: number;
  nombresPersona: string;
  apellidosPersona: string;
  fechaNacimientoPersona: string;
  emailPersona: string;
  estadoPersona: string;
  idRol: number;
  passwordPersona?: string;
}
