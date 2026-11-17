# Ajuste a los archivos de referencia

Referencias del usuario: `sm2-s11.rar` y `andina-frontend.zip` de Edisson.

El frontend mantiene componentes y servicios explícitos por entidad. Los formularios usan FormBuilder, ReactiveFormsModule y Validators; los listados usan MatTable y MatPaginator. Los servicios usan HttpClient directamente, como `streamingservice.ts` del RAR y los servicios del ZIP. Los selectores usan MatSelect, como los formularios del ZIP; las búsquedas cargan hasta 20 coincidencias.

El backend sigue Repository → ServiceInterface → ServiceImplement → Controller. Las consultas están declaradas con @Query(nativeQuery=true) en cada repositorio; los controllers construyen sus DTO con un for, como los reportes existentes. El DTO de página es una clase con atributos, constructor, getters y setters.

Se retiraron las bases CRUD, los formularios y listados heredados, el selector genérico, el generador, Criteria, EntityManager y los records añadidos. CORS utiliza el mismo patrón CorsConfiguration y UrlBasedCorsConfigurationSource del RAR, ubicado en configs/CorsConfig.java.

Se adaptan nombres, campos, relaciones y endpoints al dominio Andina. Esto conserva los patrones de los ejemplos; no significa que el código de un dominio distinto pueda copiarse literalmente sin cambios. La nota final depende de los criterios del docente.

Backend: abrir pom.xml en IntelliJ y ejecutar DemoSm2Application. Frontend: abrir frontend/ como proyecto independiente en Visual Studio Code y ejecutar npm start. PostgreSQL: localhost:5432/andina; API: localhost:8080; Angular: localhost:4200.

Los horarios validan cruces ya guardados y la BD impide repetir exactamente asignación, día y horas. No se promete exclusión atómica de rangos diferentes que se registren al mismo tiempo. Los listados mantienen límites de hasta 100 filas y búsquedas de 100 caracteres. PostgreSQL local limita las consultas a cinco segundos.

Verificación del ajuste: compilación backend/frontend, 50 comprobaciones de API y nueve pruebas del frontend. Los resultados anteriores de rendimiento corresponden a la implementación anterior y no se deben tomar como una garantía para esta versión.
