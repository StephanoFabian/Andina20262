package pe.edu.upc.demosm2.dtos;

// Cuentas por rol: total, activas, inactivas y sin contraseña
// Resultado del query nativo: cada getter recibe la columna con el mismo alias (AS rol, ...).
// El controller lo pasa al DTO con ModelMapper.
public interface CuentasPorRolQuery {
    String getRol();
    Long getTotal();
    Long getActivos();
    Long getInactivos();
    Long getSinPassword();
}
