export interface AccesoVideollamada {
  url: string;
  token: string;
  sala: string;
  identidad: string;
  titulo: string;
  traduccionDisponible: boolean;
}
export interface FragmentoTraduccion {
  id: string;
  estado?: 'pendiente' | 'procesando' | 'listo' | 'error';
  idiomaOrigen: 'es' | 'quy';
  idiomaDestino?: 'es' | 'quy';
  audioBase64?: string;
  transcripcion?: string;
  traduccion?: string;
  mensaje?: string;
  milisegundos?: number;
}
export interface SubtituloLlamada {
  id: string;
  participante: string;
  idiomaOrigen: 'es' | 'quy';
  idiomaDestino: 'es' | 'quy';
  transcripcion: string;
  traduccion: string;
  creado: number;
}
