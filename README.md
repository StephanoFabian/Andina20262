# Andina API

Backend Spring Boot con Java 21, PostgreSQL y autenticación JWT.

## Configuración

Define las variables de `.env.example` en tu terminal, IDE o en Railway → Variables.
Spring Boot no carga archivos `.env` automáticamente. No subas credenciales a Git.
Usa el **Session pooler** de Supabase (puerto 5432, SSL) para conexiones IPv4.
`JWT_SECRET` debe ser una cadena aleatoria de al menos 64 bytes para HS512.

El esquema existente se valida al arrancar (`ddl-auto=validate`); los cambios de
esquema requieren una migración previa. El proceso no crea ni borra tablas.
Cada réplica utiliza hasta cinco conexiones a PostgreSQL; revisa el límite del
pooler antes de aumentar réplicas. Este valor no constituye una prueba de capacidad.

## Ejecutar y probar

Con JDK 21 y Maven instalados, y las variables de aplicación exportadas:

```sh
mvn -DskipTests package
java -jar target/demoSM2-0.0.1-SNAPSHOT.jar
```

Las pruebas de integración usan una base PostgreSQL **descartable**, independiente
de la aplicación. Crean y eliminan su esquema. Configura `ANDINA_TEST_DB_URL`,
`ANDINA_TEST_DB_USERNAME` y `ANDINA_TEST_DB_PASSWORD`, y ejecuta `mvn clean verify`.
Por defecto conectan a `jdbc:postgresql://127.0.0.1:55439/andina_test`, usuario
`andina_test`. Las pruebas no usan `SPRING_DATASOURCE_URL` de producción.

## Colegios y aulas

Ambos recursos permiten GET de colección, GET por ID, POST, PUT por ID y DELETE
por ID, en `/api/colegios` y `/api/aula`. Crear, actualizar y eliminar requiere
`ADMIN` o `ADMIN_ESCUELA`. Las consultas requieren autenticación.

Los listados conservan la respuesta como arreglo JSON, pero ahora son paginados:
`?page=0&size=20`, con un máximo de 100 elementos por página y page de 0 a 10000.
Las cabeceras `X-Has-Next`, `X-Page` y `X-Page-Size` permiten recorrer la colección.
El cliente debe avanzar mientras `X-Has-Next` sea `true`; no asumir que la primera
respuesta contiene todos los registros. El orden es ascendente por ID.

`GET /api/aula?idColegio=1000&page=0&size=20` filtra aulas por colegio en la base
de datos. Un colegio inexistente devuelve 404. Al crear un aula, `numero` se acepta
como alias de `nombre`; las respuestas conservan el campo `nombre`.

Los IDs de creación los genera el servidor. Un ID enviado en POST se rechaza con
400. En PUT, el ID opcional del cuerpo debe coincidir con la ruta. Capacidad debe
ser positiva, el colegio debe existir y los campos de texto no pueden quedar vacíos
ni superar sus longitudes. Eliminar un colegio con relaciones existentes devuelve
409 y conserva sus dependencias.

La [revisión de historias](docs/revision-historias.md) detalla el alcance y los pendientes.

## Railway

Conecta `StephanoFabian/Andina20262`, rama `main`, usando Railpack y la raíz del
repositorio. Configura las variables anteriores en el servicio. El servidor escucha
`PORT` (8080 por defecto). Inicio: `java -jar target/demoSM2-0.0.1-SNAPSHOT.jar`.
Usa `/v3/api-docs` como comprobación HTTP de arranque y genera un dominio público.
Esta comprobación verifica que el servidor responde; no es un monitor continuo de
la base de datos. Swagger está en `/swagger-ui/index.html`; los endpoints de negocio
requieren un JWT obtenido mediante `/login`.
