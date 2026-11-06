# Frontend Andina

Angular 21 + Material. Arquitectura basada en `sm2-s11.rar`.

```bash
npm ci
npm start
```

Abre http://localhost:4200. Angular llama directamente a http://localhost:8080. La URL está en `src/environments/environment.ts` y `environment.development.ts`. El backend permite los orígenes locales mediante `configs/CorsConfig.java`.

```bash
npm run build    # dist/demoFront8228/browser
npm test        # pruebas del interceptor de autenticación
npm run format
```

Para otro despliegue, configura la URL de la API en los archivos de entorno y los orígenes permitidos en `configs/CorsConfig.java` del backend. Sirve `dist/demoFront8228/browser` y redirige las rutas Angular a `index.html`.

## Carpetas

- `src/app/models`: interfaces de DTO y catálogo de módulos.
- `src/app/services`: un servicio tipado por entidad, con los endpoints reales de Spring.
- `src/app/components/<modulo>component`: contenedor y componentes `listarcomponent`, `insertarcomponent`, `actualizarcomponent`, cada uno con su propia plantilla.
- `src/app/core`: sesión JWT, interceptor y guards.
- `src/app/shared`: comportamiento común de listas y formularios; selector remoto de referencias.

Cada entidad tiene componentes y servicios explícitos que se editan directamente. No hay un generador de pantallas.

El rol se lee del JWT para mostrar acciones; el backend valida los permisos nuevamente. La sesión se guarda en `sessionStorage`. Los selectores consultan hasta 20 coincidencias y requieren seleccionar un registro existente.

Material por curso usa la clave compuesta `idMaterial/idCurso`: permite vincular y quitar materiales. Para cambiar una vinculación, quita la existente y agrega la nueva.

## Entrega de Edisson

Materiales, Materiales por curso y Convenios se sustituyeron por las pantallas de `andina-frontend.zip`, adaptadas a esta API y con paginación en el servidor. Sus componentes se editan directamente. Antes de actualizar una base anterior, aplica `../docs/sql/integracion_edisson.sql`. Detalles en [la integración](../docs/INTEGRACION_EDISSON.md).
