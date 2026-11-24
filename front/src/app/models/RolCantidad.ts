/** Reporte GET /roles/reporte-cantidad-personas (RolCantidadDTO) */
export interface RolCantidad {
  rol: string;
  cantidad: number;
  porcentaje: number | null;
}
