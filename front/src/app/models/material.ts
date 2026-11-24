export interface MaterialDTO {
  idMaterial?: number;
  titulo: string;
  descripcion?: string | null;
  tipo?: string | null;
  urlArchivo?: string | null;
  fechaPublicacion?: string | null;
  idPersona: number;
  nombreAutor?: string;
  apellidosAutor?: string;
}
