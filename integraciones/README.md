# Videollamada con interpretación español–quechua

La generación de materiales con Llama 70B se inicia y verifica por separado:
[texto, revisión, figuras y PDF](materiales/README.md). La comparación de modelos
para mejorar reconocimiento y traducción está en [MODELOS_CANDIDATOS.md](MODELOS_CANDIDATOS.md).

La ruta `/sesiones-virtuales/sala/:id` abre una sala LiveKit asociada a una
sesión virtual existente. Se entra desde el botón de cámara del listado.
Angular sigue siendo un proyecto independiente y Spring continúa con PostgreSQL
local. No se modifican los módulos de Edisson ni se reintroducen los módulos
excluidos. La integración de video y modelos es una ampliación solicitada
expresamente; los controllers, DTO y servicios siguen separados.

## Qué hace

Cada participante puede publicar cámara, micrófono y, tras activarlo, la
traducción de su propio micrófono. Se capturan fragmentos independientes de seis
segundos; la API local reconoce el habla, traduce y sintetiza voz. Los subtítulos
se envían a los demás participantes por LiveKit y la voz sintetizada viaja como
una pista de audio distinta. El chat a la derecha del video muestra el original
y la traducción en bloques separados, con idioma, participante y hora. Sigue
los mensajes nuevos sin mover la vista de quien está leyendo mensajes anteriores;
en móvil aparece debajo del video. Cada receptor elige su idioma, subtítulos
en el video, voz traducida y audio original. La salida sintetizada no se reproduce en el altavoz
del emisor. Se recomiendan audífonos para evitar recapturar a otros participantes.

La traducción es experimental y se limita a **español y quechua de Ayacucho
(quy)**. Los tres pasos están configurados para esa misma variedad. No equivale
a un intérprete humano ni se ha validado con hablantes. El audio sintetizado
usa las voces de los modelos; no clona la voz del participante. El retardo incluye
la captura del fragmento, la espera y la inferencia; no se promete simultaneidad.

## Componentes y pesos

| Componente | Modelo / software | Licencia publicada |
|---|---|---|
| Video y audio | LiveKit Server 1.13.11 / livekit-client 2.23.0 | Apache 2.0 |
| Reconocimiento | facebook/mms-1b-all, adaptadores spa y quy | CC BY-NC 4.0 |
| Traducción | facebook/nllb-200-distilled-600M, spa_Latn ↔ quy_Latn | CC BY-NC 4.0 |
| Voz española | facebook/mms-tts-spa | CC BY-NC 4.0 |
| Voz quechua | facebook/mms-tts-quy | CC BY-NC 4.0 |

Los pesos son descargables y se ejecutan localmente. Las revisiones exactas
están fijadas en `ia/modelos.json`. **Estos modelos de Meta tienen restricción
no comercial**; un despliegue comercial necesita componentes y datos con
licencias adecuadas. Este cambio integra modelos existentes; no afirma haber
entrenado un modelo nuevo ni que los resultados sean lingüísticamente correctos.

## Iniciar en esta PC

Se necesitan Java 21, PostgreSQL, Node compatible con Angular 21, Python 3.12 y
FFmpeg. La preparación reutiliza el entorno instalado de voz para Torch/ROCm y
Transformers; no lo modifica. Solo instala dependencias ausentes en `ia/.deps`.
Los pesos quedan en la caché de Hugging Face, fuera de Git. Para otro equipo,
prepara un entorno con `ia/requirements.txt` y define `ANDINA_IA_PYTHON` con la
ruta de su Python. Para fijar el ejecutable Java usa `ANDINA_JAVA`.

Desde la raíz del repositorio:

```sh
python3 integraciones/local.py preparar
```

La primera preparación descarga varios GB de modelos. Genera claves aleatorias
en `integraciones/.local/config.json`, con permisos 600 y excluidas de Git.
Después, inicia en terminales separadas:

```sh
python3 integraciones/local.py video
python3 integraciones/local.py ia
```

Compila Spring con Java 21 y ejecútalo con la configuración local generada:

```sh
bash ./mvnw -DskipTests package
python3 integraciones/local.py backend
```

Para arrancarlo desde IntelliJ, agrega como variables de entorno las entradas
de `.local/config.json`. Las propiedades de conexión de PostgreSQL siguen en
`src/main/resources/application.properties`. No inicies dos backends en 8080.

Desde Visual Studio Code, en `frontend`:

```sh
npm ci
npm start
```

Abre `http://localhost:4200`, inicia sesión y entra a una sesión virtual con el
icono de cámara. Para una prueba de dos personas usa dos perfiles de navegador
con cuentas diferentes; una misma identidad en dos pestañas puede sustituir
su conexión anterior en LiveKit. Acepta cámara y micrófono. En cada participante
selecciona «Yo hablo» y pulsa «Compartir traducción de mi voz»; el receptor elige
el idioma que quiere escuchar.

`ANDINA_IA_DEVICE=cpu` es la opción predeterminada del código. `cuda` permite
usar un Torch CUDA o ROCm compatible; se debe verificar el rendimiento en cada
equipo. La síntesis usa CPU de forma independiente, porque resultó más rápida
en el equipo probado; `ANDINA_TTS_DEVICE` permite cambiarla. `ANDINA_IA_THREADS`
limita los hilos de CPU. Cambia el entorno o el archivo
local de configuración antes de iniciar el servicio. Tras la preparación, la ejecución de IA usa los snapshots locales y tiene
el modo sin conexión de Hugging Face activado. El primer fragmento carga
modelos y puede tardar más; los siguientes reutilizan esos pesos.

## Límites y recuperación

- El token de video dura cinco minutos para el ingreso, está firmado en Spring
  y permite entrar a una sola sala. Los identificadores de sala y participante
  son opacos. La clave de firma nunca se entrega a Angular.
- Se exige el JWT de Andina para cada acceso o consulta. La política de lectura
  de sesiones sigue siendo la del proyecto: usuarios autenticados. Restringir
  salas por matrícula exige una política adicional de permisos.
- Audio de entrada: máximo 1 MB y ocho segundos procesados; el cliente envía
  seis segundos. El backend limita el cuerpo antes de deserializar. La API de
  IA solo escucha en localhost y exige una clave exclusiva del backend.
- Un trabajador ejecuta la inferencia, con hasta 16 fragmentos pendientes y
  dos por usuario. Hasta ocho peticiones HTTP simultáneas. La llamada continúa
  si se satura la traducción; el cliente avisa y pausa la captura de fragmentos.
  Durante esa pausa no se traduce lo hablado: conviene hablar por frases.
- SQLite conserva pendientes y resultados; los estados en proceso se recuperan
  tras reiniciar, con hasta dos intentos. El UUID de cada fragmento es idempotente;
  reutilizarlo con otro contenido responde 409. Los resultados se aíslan mediante
  sesión y usuario. Al recargar, el navegador recupera hasta dos identificadores
  pendientes durante tres minutos; no guarda copias del audio en sessionStorage.
- Se conservan hasta 256 trabajos. El audio de entrada se elimina al terminar;
  resultados, subtítulos y voz se limpian por lotes después de 20 minutos al
  recibir solicitudes nuevas. Si no hay nuevas solicitudes, permanecen en el
  archivo local hasta la próxima limpieza. No se registra el audio en logs.
- La vista conserva los últimos 20 subtítulos y descarta paquetes duplicados.
  Los subtítulos compartidos en vivo no se recuperan para quien entra tarde.
- LiveKit tiene un límite configurado de 20 participantes. **Es un límite de
  configuración, no una capacidad medida.** Las pruebas funcionales usan dos.

## Uso desde otras redes

La configuración incluida enlaza LiveKit a localhost y permite probar dos
navegadores de la PC. Para dos dispositivos o acceso desde Internet hay que
configurar un dominio HTTPS, LiveKit con WSS y direcciones ICE alcanzables,
TURN/TLS cuando corresponda, el origen CORS y la URL del backend Angular.
No basta cambiar `localhost` por una IP: cámara y micrófono requieren un contexto
seguro. No se abren puertos del router ni se publica un servicio automáticamente.

## Verificación

```sh
python3 -m unittest discover -s integraciones/ia -p 'test_*.py'
bash ./mvnw -Dtest=VideollamadaServiceTests test
cd frontend
npm test
npm run build
```

`tools/verificar_voz.py` comprueba el circuito con audio sintético y una API
conectada a una BD ficticia. Genera un informe con tiempos y WAV en ambos idiomas;
no calcula precisión de habla natural ni calidad de traducción. Los resultados
medidos y los límites de la prueba se registran en `VERIFICACION.json`.

## Referencias oficiales

- https://docs.livekit.io/transport/self-hosting/local/
- https://docs.livekit.io/frontends/reference/tokens-grants/
- https://docs.livekit.io/transport/data/packets/
- https://huggingface.co/facebook/mms-1b-all
- https://huggingface.co/facebook/nllb-200-distilled-600M
- https://huggingface.co/facebook/mms-tts-spa
- https://huggingface.co/facebook/mms-tts-quy
