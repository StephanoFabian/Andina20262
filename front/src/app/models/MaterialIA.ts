export interface MaterialIA {
  id: string;
  tema: string;
  nivel: string;
  instrucciones?: string;
  estado:
    | 'pendiente'
    | 'redactando'
    | 'texto_listo'
    | 'ilustrando'
    | 'listo'
    | 'error';
  titulo?: string;
  texto?: string;
  textoRevisado?: string;
  mensaje?: string;
  archivos?: string[];
  modeloTexto?: string;
  modeloFiguras?: string;
  segundos?: number;
}
