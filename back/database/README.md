# PostgreSQL local para Andina

`andina_local.sql` contiene las 18 tablas del proyecto y 75 registros ficticios relacionados. Incluye claves primarias, claves foráneas y secuencias listas para seguir registrando datos.

## Importar en una base nueva

Desde la carpeta del backend:

```bash
createdb -h localhost -p 5432 -U postgres andina_demo
psql -h localhost -p 5432 -U postgres -d andina_demo -f database/andina_local.sql
```

También puedes crear `andina_demo` en pgAdmin y ejecutar el archivo con la herramienta PSQL. El archivo incluye comandos de `psql`; para usar el editor Query Tool de pgAdmin, utiliza `andina_pgadmin.sql`.

Para conectar el backend a esa base local, en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/andina_demo
spring.datasource.username=postgres
spring.datasource.password=TU_CLAVE_LOCAL
```

Conserva tu contraseña local. El frontend sigue en `http://localhost:4200` y la API en `http://localhost:8080`.

## Acceso de demostración

- Administrador: **ID 1**, contraseña **AdminAndina2026!**.
- Director, especialista, local, docentes y estudiantes: **ID 2 a 10**, contraseña **AndinaDemo2026!**.

Las contraseñas de la tabla Persona son hashes BCrypt. Los correos, nombres, enlaces y demás datos son ficticios; los enlaces de clases y materiales son ejemplos.

El archivo se importa en una base vacía. No contiene `DROP DATABASE` ni `DROP TABLE` y no modifica la base local que ya tienes.
