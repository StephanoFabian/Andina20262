export interface ConvenioDTO {
  idConvenio?: number;
  nombre: string;
  entidad: string;
  /** GOBIERNO_REGIONAL, COOPERACION_INTERNACIONAL u OTRO */
  tipoEntidad: string;
  region: string;
  /** yyyy-MM-dd */
  fechaInicio: string;
  /** yyyy-MM-dd */
  fechaFin: string;
  presupuesto: number;
  /** VIGENTE, EN_RENOVACION, VENCIDO o FINALIZADO */
  estado?: string | null;
}

export const TIPOS_ENTIDAD: Record<string, string | undefined> = {
  GOBIERNO_REGIONAL: 'Gobierno regional',
  COOPERACION_INTERNACIONAL: 'Cooperación internacional',
  OTRO: 'Otro',
};

export const ESTADOS_CONVENIO: Record<string, string | undefined> = {
  VIGENTE: 'Vigente',
  EN_RENOVACION: 'En renovación',
  VENCIDO: 'Vencido',
  FINALIZADO: 'Finalizado',
};
