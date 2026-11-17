export interface Asistencia {
  idAsistencia?: number;
  idSesionVirtual: number;
  idPersona: number;
  estado: string;
  observacion?: string;
}
