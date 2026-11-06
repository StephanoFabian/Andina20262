# Integración del frontend de Edisson

Fuente: `/home/nathaniel-r/Descargas/andina-frontend.zip`. SHA-256: `82bbd48fdd02b34f252d387009e481a01fa0e9d5dd7197ddf955db044e2781a2`.

Se sustituyen únicamente Materiales, Materiales por curso y Convenios: componentes, formularios, listados, consultas y reportes de esos tres módulos, con sus modelos y auxiliares de confirmación, validación, paginador y gráfico de barras. El resto de las pantallas, el login y la navegación se mantienen. Auditoría, Encuesta, RespuestaEncuesta y CapacitaciónDocente siguen excluidos.

La versión anterior se conserva en el commit `2f2beda`. La integración se realiza y publica únicamente en `stephano_01`.

## Adaptaciones necesarias

- Los servicios usan `Injectable` y la sesión JWT existente (`andina.token`); no se introduce una segunda sesión.
- Se conectan los endpoints reales de esta rama y se conservan los alias de rutas `listar`, `insertar` y `editar/:id`, además de las rutas originales `nuevo` y `ediciones/:id` del ZIP.
- Los listados y reportes usan paginación en el servidor (máximo 100 filas); los selectores de referencias y las sugerencias de tipos consultan hasta 20 coincidencias. No se descargan directorios completos de personas, materiales ni cursos. Los nombres de autor/material/curso se obtienen en la misma proyección del listado.
- Las búsquedas de materiales usan prefijo; la etiqueta del formulario lo indica. Las tablas y los gráficos de los reportes representan la página solicitada.
- Se incorpora el estilo de los componentes del ZIP dentro de `.pagina`, manteniendo los estilos de los demás compañeros.
- El generador omite los módulos marcados con `source: "edisson"`. Sus archivos se editan directamente en `components/material`, `components/materialcurso`, `components/convenio` y los servicios correspondientes. Los módulos restantes siguen generándose desde `modulos.json`.

## Convenios y compatibilidad

Se admiten los campos del compañero: `entidad`, `tipoEntidad`, `region`, `presupuesto` y los estados `VIGENTE`, `EN_RENOVACION`, `VENCIDO`, `FINALIZADO`. Se mantienen `institucion`, `idColegio` y `CANCELADO` para clientes y registros anteriores. `entidad` e `institucion` representan el mismo valor almacenado. Editar un convenio anterior desde la pantalla nueva conserva su colegio.

Los datos adicionales de los convenios antiguos permanecen nulos hasta completarlos; no se inventan presupuestos ni regiones. El nuevo contrato permite convenios sin colegio, como el formulario del ZIP.

Antes de usar esta versión sobre una base anterior, aplica con `psql -f docs/sql/integracion_edisson.sql` la migración aditiva e idempotente. Agrega los campos y los índices necesarios, permite el colegio opcional y conserva los datos existentes. `ddl-auto=update` por sí solo no quita la restricción NOT NULL anterior.

## Rutas nuevas

- `/materiales/consultas`: materiales por título o curso.
- `/materiales/reporte-tipo`: cantidades por tipo y gráfico.
- `/materiales-cursos/reporte`: cantidades por curso y gráfico.
- `/convenios/por-vencer`: vencidos y próximos a vencer, con plazo, presupuesto y días restantes.

Los enlaces están dentro de los listados de sus respectivos módulos.

## Verificación realizada

- Compilación de producción y nueve pruebas unitarias del frontend.
- Arranque y pruebas Maven sobre una base aislada.
- Las 40 comprobaciones previas de API siguen pasando; diez comprobaciones nuevas cubren el convenio sin colegio, campos nuevos, edición compatible con registros anteriores, asociaciones, filtros, reportes, sugerencias y límites.
- Navegador: crear/editar/eliminar material y convenio, asociar/quitar material de curso, autor predeterminado, pantallas de reporte, vista móvil a 390 px y acceso a Grados. Sin errores JavaScript ni desbordamiento horizontal.
- Carga: 10.000 materiales, 10.000 convenios y 10.000 relaciones material-curso; 200 lecturas, 20 clientes simultáneos, páginas de 50 filas. p50 28.36 ms, p95 49.25 ms y máximo 65.49 ms, sin errores. Es una prueba local con caché caliente, una instancia y PostgreSQL 18.6; no garantiza capacidad de producción. Se mantienen los límites de OFFSET y conteo exacto documentados en FRONTEND.md.

Los resultados y capturas de esta integración están en `docs/verificacion/edisson-*`.

La compilación final genera aproximadamente 709 kB iniciales (164 kB estimados en transferencia). Supera en 9 kB el aviso de 700 kB del proyecto; permanece por debajo del límite de error de 1 MB.
