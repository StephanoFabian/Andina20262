# Frontend Andina

Angular 21 + Material. Arquitectura basada en `sm2-s11.rar`.

```bash
npm ci
npm start
```

Abre http://localhost:4200. La API debe estar en http://localhost:8080. El proxy local transforma `/backend/*` en `/*`. Para cambiar el puerto de la API, edita `proxy.conf.json`.

```bash
npm run build    # dist/demoFront8228/browser
npm test        # pruebas del interceptor de autenticación
npm run format
```

En producción sirve el directorio `dist/demoFront8228/browser`, redirige las rutas Angular a `index.html` y configura un proxy `/backend/*` hacia el backend quitando `/backend`. No es necesario un servidor SSR.

## Carpetas

- `src/app/models`: interfaces de DTO y catálogo de módulos.
- `src/app/services`: un servicio tipado por entidad, con los endpoints reales de Spring.
- `src/app/components/<modulo>component`: contenedor y componentes `listarcomponent`, `insertarcomponent`, `actualizarcomponent`, cada uno con su propia plantilla.
- `src/app/core`: sesión JWT, interceptor y guards.
- `src/app/shared`: comportamiento común de listas y formularios; selector remoto de referencias.

`modulos.json` es la fuente del catálogo y de los componentes generados. Para cambiar campos, edítalo y ejecuta `python3 ../tools/generar_frontend.py` y `npm run format`. El generador conserva el código de `core/`, `shared/`, login e inicio.

El rol se lee del JWT para mostrar acciones; el backend valida los permisos nuevamente. La sesión se guarda en `sessionStorage`. Los selectores consultan hasta 20 coincidencias y requieren seleccionar un registro existente.

Material por curso usa la clave compuesta `idMaterial/idCurso`: permite vincular y quitar materiales. Para cambiar una vinculación, quita la existente y agrega la nueva.
