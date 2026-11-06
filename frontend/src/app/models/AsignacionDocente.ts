import { Fila } from './modulo';
export interface AsignacionDocente extends Fila {
  id_asignacion?: number;
  id_aula: string;
  id_curso: number;
  id_periodo: number;
  id_persona: number;
  id_colegio: number;
  modalidad: string;
  horassemanales: number;
}
