export interface Pagina<T> {
  content: T[];
  totalElements: number;
  number: number;
  size: number;
}
export interface Modulo {
  key: string;
  name: string;
  label: string;
  owner: string;
  path: string;
  id: string;
  roles: string[];
  editable: boolean;
}
