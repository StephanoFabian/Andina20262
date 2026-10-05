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

Con JDK 21 y Maven instalados, y las variables exportadas:

```sh
mvn clean verify
java -jar target/demoSM2-0.0.1-SNAPSHOT.jar
```

La prueba de contexto valida la conexión y el esquema de la base configurada.
Para desarrollo y CI, usa una base de pruebas con el esquema previamente creado.

## Railway

Conecta `StephanoFabian/Andina20262`, rama `main`, usando Railpack y la raíz del
repositorio. Configura las variables anteriores en el servicio. El servidor escucha
`PORT` (8080 por defecto). Inicio: `java -jar target/demoSM2-0.0.1-SNAPSHOT.jar`.
Usa `/v3/api-docs` como comprobación HTTP de arranque y genera un dominio público.
Esta comprobación verifica que el servidor responde; no es un monitor continuo de
la base de datos. Swagger está en `/swagger-ui/index.html`; los endpoints de negocio
requieren un JWT obtenido mediante `/login`.
