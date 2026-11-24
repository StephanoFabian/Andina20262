> Registro histórico de la versión inicial. Para la implementación actual consulta PATRONES_REFERENCIA.md.

# Verificación del frontend · 6 de noviembre de 2026

Pruebas contra una base PostgreSQL independiente: `andina_frontend_verificacion_20261106`. No se modificaron registros de la base de trabajo `andina`.

- Backend: `mvn test` con Java 21 y la URL de la base de prueba. Contexto Spring y las 18 interfaces de repositorio cargan correctamente.
- Frontend: `npm run build` de producción con TypeScript y plantillas estrictas. Carga inicial: 673,69 kB sin comprimir; transferencia estimada: 154,35 kB. Módulos cargados bajo demanda.
- Frontend: cuatro pruebas unitarias del interceptor: token a la API propia, ausencia de token a dominios externos, cierre por 401 y conservación del formulario cuando el login rechaza credenciales.
- API: 40 comprobaciones, con creación, consulta, actualización y eliminación de los 18 módulos (Material por curso usa vinculación/eliminación). Se verifican IDs relacionados, paginación, permisos docentes, duplicados, referencias inexistentes y rangos inválidos.
- Concurrencia de horarios: ocho escrituras simultáneas con la misma asignación, día y rango. Una respuesta 201 y siete respuestas 400. La comprobación toma un bloqueo transaccional de la asignación, y no un bloqueo global.
- Chromium: las 36 rutas de listado e inserción abren; crear/editar/eliminar un grado funciona; cancelar la eliminación conserva el registro; menú móvil y logout funcionan; sin errores JavaScript ni desbordamiento horizontal a 390 px. También se crea y edita un aula seleccionando colegio mediante búsqueda; al editar se conserva el colegio seleccionado.

## Medición de paginación

10.000 grados; una instancia de API y PostgreSQL 18.6 en localhost. Después de 10 consultas de calentamiento: 200 peticiones, 20 clientes concurrentes, páginas de 50 registros y alternancia entre listado y búsqueda por prefijo. Total: 0,262 s. Mediana: 23,41 ms; p95: 40,74 ms; máximo: 50,37 ms. Cero errores.

Es una medición de lectura local sobre ese conjunto, con cachés calientes. No mide capacidad de producción, conexiones de red, escrituras sostenidas ni millones de registros. La implementación conserva OFFSET y un conteo exacto; sus límites se describen en `FRONTEND.md`.

## Reproducir

Crea una base desechable, configura la API para usarla y registra un administrador de prueba con BCrypt. Los scripts no crean usuarios administradores en la base de trabajo.

```bash
# API de prueba ejecutándose en localhost:18081
export ANDINA_TEST_PERSONA=1
export ANDINA_TEST_PASSWORD='contraseña-del-administrador-de-prueba'
export ANDINA_TEST_WRITES=1
python3 tools/verificar_api.py

# Medición: requiere sembrar los 10.000 grados de prueba y aplicar los índices.
python3 tools/medir_paginacion.py
```

Los scripts guardan sus resultados en `/tmp/andina-verificacion`. Copias de la ejecución y capturas están en [verificacion/](verificacion/).
