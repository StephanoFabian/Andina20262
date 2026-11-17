/** Reporte GET /personas/reporte-cuentas-por-rol (CuentasPorRolDTO). Solo ADMIN. */
export interface CuentasPorRol {
  rol: string;
  total: number;
  activos: number;
  inactivos: number;
  sinPassword: number;
}
