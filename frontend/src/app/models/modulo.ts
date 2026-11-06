export type Fila = Record<string, string | number | null | undefined>;
export interface Pagina<T> {
  content: T[];
  totalElements: number;
  number: number;
  size: number;
}
export interface Campo {
  key: string;
  label: string;
  type: string;
  required: boolean;
  resource?: string;
  options?: (string | { value: number; label: string })[];
  min?: number;
  max?: number;
  minLength?: number;
  maxLength?: number;
}
export interface Modulo {
  key: string;
  name: string;
  label: string;
  owner: string;
  path: string;
  id: string;
  fields: Campo[];
  createPath: string;
  updatePath: string;
  deletePath: string;
  roles: string[];
  editable: boolean;
  source?: string;
}
