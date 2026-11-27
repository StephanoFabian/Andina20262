/** Datos del registro público (POST /registro). El rol y el estado los pone el backend. */
export interface Registro {
  nombresPersona: string;
  apellidosPersona: string;
  fechaNacimientoPersona: string;
  emailPersona: string;
  passwordPersona: string;
}
