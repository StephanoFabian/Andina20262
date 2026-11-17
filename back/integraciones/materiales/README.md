# Materiales: texto de 70B → revisión e ilustración → PDF

En Andina: **Materiales → Crear con IA** (`/materiales/crear-con-ia`).
El usuario escribe tema, nivel e indicaciones. El servicio conserva el borrador
original y permite verlo antes de terminar la segunda etapa.

1. `llama3.3:70b-1bit`, ya instalado en Ollama, redacta texto en español. Su
   formato instalado es GGUF **IQ1_S**, con aproximadamente 70B parámetros.
2. `qwen3:14b` recibe todo ese texto. Revisa terminología y cálculos y propone
   figuras en un esquema JSON. Se conserva también esta versión revisada.
3. Herramientas locales calculan la geometría y exportan imágenes PNG/SVG,
   PDF y, si LibreOffice está disponible, Word. No se ejecuta código del modelo.

El texto original se descarga como `material.txt`, y la revisión como
`material-revisado.txt`. El PDF usa el texto revisado. La exportación de
geometría recalcula medidas; eso **no certifica todas las afirmaciones del
texto libre**. En la primera prueba, el 70B IQ1_S dio definiciones y resultados
incorrectos. La revisión humana del material sigue siendo necesaria.

## Iniciar

Requisitos existentes: Ollama, ambos modelos ya descargados, Python con Pillow
y ReportLab. La exportación Word usa LibreOffice en un perfil temporal separado.
En esta PC esas herramientas ya están instaladas; no se modifican los entornos
de voz, música ni los modelos existentes. Para otro equipo, `requirements.txt`
registra las versiones de Pillow y ReportLab usadas; instala esas dependencias
en un entorno separado y selecciona su Python con `ANDINA_MATERIAL_PYTHON`.

Desde la raíz del repositorio, en una terminal:

```sh
python3 integraciones/local.py materiales
```

Compila Spring con Java 21 y usa el lanzador que carga las claves locales:

```sh
bash ./mvnw -DskipTests package
python3 integraciones/local.py backend
```

El frontend sigue iniciándose aparte en Visual Studio Code con `npm start`.
Para IntelliJ, carga `ANDINA_MATERIAL_IA_URL` y `ANDINA_MATERIAL_IA_API_KEY`
desde `integraciones/.local/config.json`. La clave se genera localmente y no
se guarda en Git ni se entrega al navegador.

## Permisos concedidos a la cadena

Los modelos pueden proponer texto y datos geométricos. El servicio puede crear
archivos únicamente dentro de `integraciones/.local/materiales/<id-opaco>/`.
Las herramientas de exportación y los nombres de archivo están fijados por el
código. No hay herramientas de shell para el modelo, ejecución de Python/SVG
generado, instalación de paquetes, lectura de documentos personales ni envío
de mensajes. Las figuras se dibujan desde datos validados, con texto escapado.

La API exige el JWT y los mismos roles de escritura que Materiales:
ADMIN, ADMIN_ESCUELA, ESPECIALISTA o LOCAL. Los resultados y archivos quedan
aislados por usuario. La generación no publica un registro en el catálogo de
Materiales automáticamente: entrega un borrador y los archivos descargables.
Los materiales de Edisson y el CRUD existente conservan su comportamiento.

## Figuras soportadas

- Triángulos con tres lados: desigualdad triangular, coordenadas por ley de
  cosenos y área por Herón. Se rechazan triángulos excesivamente planos.
- Triángulos rectángulos: catetos positivos; hipotenusa recalculada por Pitágoras.
- Rectángulos y círculos: medidas positivas, área y perímetro recalculados.
- Hasta tres figuras por material, con valores finitos y de hasta 1000 unidades.

Las figuras que no pertenecen a estos tipos requieren otra herramienta. Si la
segunda IA no encuentra una figura pertinente, puede entregar un PDF de texto.
No se usa difusión para dibujar longitudes o ángulos: las figuras geométricas
se construyen a escala desde sus medidas y se descargan también como SVG.

## Límites y recuperación

El servicio de materiales escucha en localhost:8096, separado de la voz en
8095. SQLite conserva cada etapa. Tras un reinicio se repite solo la etapa
interrumpida y se conserva el borrador que ya se generó. El UUID del cliente es
idempotente: repetir la misma solicitud no duplica un trabajo; cambiarla con el
mismo UUID responde 409.

Hay un trabajador, hasta cuatro materiales pendientes, uno por usuario, ocho
peticiones HTTP simultáneas y hasta 100 materiales almacenados. Ese último
límite requiere archivado administrativo al alcanzarse; no se borran archivos
del usuario automáticamente. Cada etapa tiene hasta dos intentos tras reinicios.
Las solicitudes están limitadas a 20 KB; cada descarga, a 8 MB.

Cada llamada al modelo tiene un contexto de 2048 tokens, hasta 512 tokens de
salida y un plazo de diez minutos. La configuración predeterminada usa cuatro
hilos de CPU y hasta 16 capas en GPU; se puede ajustar con:

```text
ANDINA_MATERIAL_MODEL=llama3.3:70b-1bit
ANDINA_MATERIAL_FIGURE_MODEL=qwen3:14b
ANDINA_MATERIAL_THREADS=4
ANDINA_MATERIAL_GPU_LAYERS=16
ANDINA_MATERIAL_FIGURE_GPU_LAYERS=16
```

Ollama descarga de memoria el modelo al terminar cada etapa (`keep_alive: 0`).
Las colas separadas evitan que la generación bloquee los endpoints de voz,
pero CPU, RAM y GPU son recursos compartidos: **no se promete la misma latencia
de videollamada bajo generación simultánea**. El 70B en IQ1_S sacrifica precisión
y puede tardar varios minutos. Los límites configurados no son capacidad medida.

## Verificar

```sh
python3 -m unittest discover -s integraciones/materiales -p 'test_*.py'
bash ./mvnw -Dtest=MaterialIAServiceTests test
```

Las pruebas comprueban geometría, exportaciones, aislamiento de usuarios,
límites, idempotencia y conservación de etapas. La prueba de modelos reales y
los tiempos medidos se describen en `VERIFICACION.json`.

En la muestra real el redactor tardó unos tres minutos y la revisión final
del 14B unos 44 segundos de carga y evaluación. Hubo repeticiones de la segunda
etapa durante la corrección; no se presentan como un benchmark de generación
continua. Las consultas de un material terminado se probaron con cuatro
clientes y 40 peticiones: cero errores, mediana 8,03 ms y percentil 95 de
11,64 ms. Esto no mide generación en paralelo ni simultaneidad con traducción.

`tools/verificar_material_ia.py` comprueba la interfaz, las seis descargas, las
imágenes incrustadas en Word, la recuperación al recargar y una vista de 390 px.
Usa una cuenta de una BD ficticia con `ANDINA_MATERIAL_TEST_PASSWORD` y el UUID
de un material ya terminado en `ANDINA_MATERIAL_TEST_ID`; no inicia otro modelo.

Referencias:
- https://huggingface.co/meta-llama/Llama-3.3-70B-Instruct
- https://huggingface.co/Qwen/Qwen3-14B
- https://docs.ollama.com/capabilities/structured-outputs
- https://docs.ollama.com/api/generate
