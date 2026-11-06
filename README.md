# Andina · Gestión educativa

Backend Spring Boot y frontend Angular 21 con Angular Material, en la rama `stephano_01`.

El frontend sigue la separación del ejemplo `sm2-s11.rar`: **models → services → components**, con componentes contenedores y rutas hijas `listar`, `insertar` y `editar/:id`. Incluye login JWT, navegación adaptable a móvil, formularios reactivos, confirmación de eliminación y listados con paginación en el servidor.

## Ejecutar

1. Configura PostgreSQL en `src/main/resources/application.properties` o con `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`. Requiere Java 21.
2. Inicia el backend desde IntelliJ o con `mvn spring-boot:run`, en el puerto 8080.
3. En otra terminal: `cd frontend`, `npm ci`, `npm start`. Usa Node 22.12+ y npm 10+.
4. Abre http://localhost:4200 e ingresa el ID de persona y su contraseña ya registrada. No se crean administradores ni contraseñas de demostración en la base de trabajo.

La primera ejecución crea las tablas nuevas mediante la configuración `ddl-auto=update` existente. No borra tablas de módulos anteriores. Para búsquedas en tablas grandes, aplica después los índices de [docs/sql/indices_busqueda.sql](docs/sql/indices_busqueda.sql) con `psql -f` fuera de una transacción.

Consulta [la arquitectura y el alcance](docs/FRONTEND.md) y [los resultados de verificación](docs/VERIFICACION_FRONTEND.md).
