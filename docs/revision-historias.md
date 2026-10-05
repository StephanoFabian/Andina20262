# Revisión de historias de usuario de Andina

Fuente: `Andina_Historias_de_Usuario_Completas.docx`, exportación del 04/10/2026:
62 tarjetas HU y 9 historias dentro de épicos archivados. Las marcas de Trello
describen el tablero; no sustituyen una prueba del código. HU44 aparece con dos
alcances y se distingue por su título.

## Colegios y aulas: cambios implementados y probados

| Historias | Resultado |
|---|---|
| HU01, HU35, HU55–HU58 | CRUD completo de colegios, validación de campos geográficos y longitudes, permisos de escritura, consulta individual y protección de relaciones al eliminar. |
| HU36, HU51–HU54, HU74 | CRUD de aulas, colegio obligatorio y existente, capacidad positiva, actualización, consulta por colegio y permisos de eliminación. |
| HU75 | Se cubre el CRUD de aulas descrito en los criterios. El título menciona equipamiento, pero sus criterios no especifican campos de equipos; no se inventan. |
| HU04 | Swagger incorpora los nuevos endpoints y documenta filtros, paginación y conflictos de borrado. |
| HU05 | Siete pruebas de integración HTTP con PostgreSQL real. Esto no demuestra la cobertura global superior al 80% solicitada por la historia. |

En aulas se mantiene `nombre` por compatibilidad con el código desplegado y se
acepta `numero` como alias de entrada. La respuesta conserva `nombre`. No se
modifican entidades ni tablas en esta entrega.

Se corrigió el uso de `@NotBlank` en campos numéricos y la creación de aulas que
consultaba el colegio pero no asignaba explícitamente la relación. POST ya no
puede actualizar un registro existente mediante un ID enviado por el cliente.

Los listados consultan segmentos ordenados y limitados de la base de datos,
sin obtener toda la colección ni ejecutar un conteo total. Aula carga su colegio
en la consulta para evitar una consulta adicional por cada resultado. Se conserva
la respuesta como arreglo; los consumidores deben usar `page`, `size` y
`X-Has-Next`. El tamaño máximo es 100. Se verificaron 215 aulas: 205 de un colegio
se recuperan en páginas de 100, 100 y 5; las otras 10 se excluyen del filtro.
No se realizaron pruebas de carga concurrente ni se afirma capacidad de producción.

## Otros requisitos revisados

| Historias | Situación observada / trabajo pendiente |
|---|---|
| HU06 | Falta el modelo y la política de conectividad mínima por colegio. Requiere definir umbrales. |
| HU07, HU25–HU29, HU59 | Existe administración de asignaciones; quedan por revisar el vínculo real con aula y los conflictos de horarios. |
| HU08 | Los reportes de matrícula no sustituyen asistencia y calificaciones por sesión. Falta ese modelo. |
| HU09 | Faltan el flujo de generación y revisión de ejercicios adaptativos y su auditoría. |
| HU10 | Falta cliente con almacenamiento persistente y sincronización offline. |
| HU11 | Falta el módulo de convenios, vigencias y presupuestos. |
| HU12 | Hay documentación de despliegue; faltan panel, métricas y alertas del alcance completo. |
| HU13 | Faltan capacitación y evaluación de competencias docentes. |
| HU14 | Faltan incidencias, prioridades y notificaciones. |
| HU15 | Falta la bitácora de acceso y modificaciones con consulta filtrada. |
| HU16 | Falta el módulo de encuestas y estadísticas de satisfacción. |
| HU17, HU44 materiales, HU48 | Existen materiales, autores y asociaciones a cursos; requieren pruebas específicas de validación y permisos para certificar todos sus criterios. |
| HU18, HU19 | Faltan la interfaz de participación, sesiones y sus pruebas. |
| HU20–HU24, HU60 | Existen endpoints de cursos y consulta por área. No se certifica su CRUD mediante las pruebas de esta entrega. |
| HU30, HU38, HU44 reasignación, HU47 | Persona tiene rol pero no relación con aula; falta reasignación y filtrado por aula/estado. |
| HU31, HU41 | Existe CRUD de periodos. Deben completarse y probarse las reglas de fechas y periodo activo único. |
| HU32, HU40 | Existe catálogo de grados enlazado a detalle de matrícula; falta cobertura específica. |
| HU33, HU39, HU46 | Perfil académico actual no incluye la relación personal y seguimiento psicológico descritos. Falta la actualización parcial y sus permisos de lectura. |
| HU34, HU42, HU43 | Existen matrícula y detalle; quedan por verificar la restricción a estudiantes y la prevención de duplicados por periodo. |
| HU37 | Existe catálogo de roles. La respuesta de integridad referencial ahora se traduce a 409; faltan pruebas específicas de este flujo. |
| HU45 | Existe asociación al rol; deben alinearse ruta, códigos de error y estado por defecto con los criterios exactos. |
| HU49, HU50 | Sin criterios en el documento; requieren aclaración antes de declarar cumplimiento. |
| HU66 | Archivada y sin criterios; no se inventa un alcance. |
| Épicos 3, 4, 5 y 6 | Las nueve historias de videoconferencia, ejercicios, sesiones y avance requieren módulos adicionales. No se consideran completadas por los CRUD existentes. |

## Pruebas de esta entrega

`ColegiosAulasIntegrationTests` valida persistencia del CRUD, cambio de colegio de
un aula, alias `numero`, errores 400/404/409, protección de relaciones, permisos
ADMIN/ADMIN_ESCUELA frente a DOCENTE, autenticación obligatoria, protección de IDs,
paginación ordenada, límite máximo y filtro por colegio. Utiliza una base de pruebas
descartable configurada mediante `ANDINA_TEST_DB_*`, independiente de Supabase.
