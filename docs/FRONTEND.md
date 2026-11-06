# Frontend Andina

Rama: `stephano_01`. Referencia: `sm2-s11.rar` (Angular 21, Material, modelos, servicios HTTP y componentes con rutas hijas para listar, insertar y actualizar).

## Alcance

Se excluyen Auditoría, Encuesta, RespuestaEncuesta y CapacitaciónDocente. No existen entidades de estos módulos en esta rama; no se ejecutan borrados en una base existente.

Asistencia y Sesión virtual: Stephano. Horario: Francisco. Convenio: Eddinson. Incidencia: delegado. Los demás responsables conservan la distribución entregada.

## Ejecución

Backend en localhost:8080. Frontend: `cd frontend`, `npm ci`, `npm start`. Abrir http://localhost:4200. El proxy `/backend` evita depender de CORS en desarrollo. Producción debe enrutar `/backend/*` hacia la API quitando el prefijo.

## Módulos y responsables

| Módulo | Responsable |
|---|---|
| Asignación docente, Curso, Horario | Francisco |
| Aula, Colegio | Samir |
| Grado, Matrícula, Detalle de matrícula, Periodo académico, Asistencia, Sesión virtual | Stephano |
| Material, Material por curso, Convenio | Eddinson |
| Persona, Rol, Perfil académico | Sebastian |
| Incidencia | Delegado |

## Integración

Se añaden cinco módulos que no existían en el punto de partida de esta rama: Asistencia, Sesión virtual, Horario, Convenio e Incidencia, con entidad, DTO validado, repositorio, interfaz de servicio, implementación y controller. Las sesiones y los horarios dependen de una asignación docente. La asistencia depende de sesión y persona, con una restricción única para ese par. Los convenios dependen del colegio; las incidencias, de persona y colegio.

El contrato anterior de asignación representa `id_aula` como nombre de aula de texto; se conserva para compatibilidad. Los horarios comprueban cruces dentro de la misma asignación y día. No comprueban solapamientos entre asignaciones distintas del mismo docente ni reemplazan una agenda personal.

Se completan GET/PUT/DELETE de colegio y PUT de aula, se corrigen las validaciones numéricas del DTO de aula y se incluyen los IDs y referencias necesarios para editar cursos y asignaciones. Los endpoints de escritura validan los roles en el servidor.

Cada módulo expone `GET <ruta>/pagina?page=0&size=10&q=`. La respuesta contiene `content`, `totalElements`, `number` y `size`. El frontend usa únicamente ese listado. La búsqueda se hace por prefijo, sin distinguir mayúsculas; `%` y `_` se interpretan como caracteres literales. Las proyecciones seleccionan los campos del DTO y no serializan entidades ni contraseñas. Material por curso conserva sus dos IDs.

La página admite tamaños de 1 a 100 y posiciones de 0 a 10000. Las búsquedas se limitan a 100 caracteres. Cada consulta SQL de listado y conteo tiene un límite de cinco segundos. Los endpoints antiguos de listado permanecen para conservar compatibilidad y no son los que consume el frontend.

Las relaciones se seleccionan mediante autocompletado remoto, con un máximo de 20 coincidencias y búsqueda al escribir. No se descargan catálogos completos para construir un formulario. Al editar, se consulta por separado la referencia seleccionada para conservar su ID aunque quede fuera de la primera página.

## Límites

La paginación usa OFFSET y un conteo exacto. A medida que crezcan los datos o aumenten las páginas profundas, ambos pueden costar más; el límite de tiempo evita consultas indefinidas. Los índices de búsqueda se entregan como SQL idempotente, aplicable sin bloquear escrituras durante su construcción. Para volúmenes que excedan lo probado se debe medir de nuevo y considerar paginación por cursor y conteos aproximados o diferidos.

Los formularios deshabilitan el botón mientras se guarda, pero no conservan borradores sin guardar al reiniciar el navegador. No se añaden colas ni trabajos en segundo plano. Los registros confirmados se conservan en PostgreSQL. Las sesiones contienen un enlace HTTPS para entrar a una videollamada; esta entrega no implementa un servidor de videoconferencias.
