import { Fila } from './modulo';
export interface Material extends Fila {
  idMaterial?: number;
  titulo: string;
  descripcion?: string;
  tipo: string;
  urlArchivo?: string;
  fechaPublicacion: string;
  idPersona: number;
}
