# Modelos revisados para Andina — 7 de noviembre de 2026

Esta comparación distingue resultados publicados por los autores de mediciones
en esta PC. No se ha medido aquí la velocidad ni la precisión de los candidatos
de traducción siguientes. Los conjuntos de prueba y las métricas distintos no
se comparan directamente.

| Función | Candidato de pesos abiertos | Observación |
|---|---|---|
| Español → quechua de Ayacucho | Rosettia GSPO NLLB 1.3B | Primera opción para evaluar frente al NLLB 600M actual. Ajustado específicamente a esa dirección; no demuestra igual mejora en quechua → español. |
| Español → quechua con más parámetros | Rosettia Qwen3.5 9B | Candidato para conversión y prueba Q4/Q5; el repositorio consultado contiene pesos completos, no un GGUF publicado verificado. |
| Traducción pequeña especializada | Rosettia Chanka 4B alpha160 | Publica resultados sobre 158 ejemplos propios; no son directamente comparables con AmericasNLP 1003. |
| Reconocimiento en español | Whisper large-v3-turbo | Candidato optimizado para velocidad respecto a Whisper large-v3. No sustituye sin más al reconocedor quechua. |

El adaptador GSPO de 1.3B publica 45.53 ChrF (`word_order=0`) en 1003 frases de
AmericasNLP 2021 para español → quy, frente a 40.55 del Qwen 9B del mismo equipo
con decodificación greedy. Son resultados de los autores, sin evaluación humana
de hablantes; no son porcentajes de exactitud. El 4B publica chrF++ en otro
conjunto: no se puede inferir que su 56.94 sea superior a 45.53 ChrF del 1.3B.

Para la GPU AMD de esta PC, FP16 en Torch/ROCm es una ruta ya probada por el
servicio actual. Q4/Q5 reducen memoria de pesos, pero no garantizan menor latencia
ni mejor precisión. En particular, la documentación de CTranslate2 describe
ejecución GPU sobre NVIDIA: no se da por hecho soporte de la RX 9060 XT.

## Autoría de documentos

Se encontraron ya instalados Llama 3.3 70B IQ1_S, Qwen3.6 35B Q4_K_M,
Qwen3.5 122B IQ1_M y otros modelos. El usuario eligió expresamente el **70B
IQ1_S** como redactor. Su primera muestra matemática tuvo errores; la segunda
etapa revisa el texto antes de dibujar y exportar. Qwen3.6 35B Q4_K_M queda
como candidato para comparar precisión con una batería de ejercicios, sin
asumir que menos parámetros significa menor calidad.

Para triángulos y otras figuras, la segunda IA produce especificaciones
geométricas. Un renderizador valida medidas y exporta PNG, SVG y PDF. Un LLM
de visión entiende imágenes, pero esa capacidad no implica generación nativa
de imágenes ni exportación directa de PDF.

Las licencias importan: Rosettia basado en NLLB y los modelos MMS actuales
publican CC BY-NC 4.0; las variantes Rosettia Qwen consultadas publican Apache
2.0. Llama 3.3 tiene su licencia comunitaria de Meta. La elección final requiere
revisar también los términos de los datos y las condiciones del despliegue.

Fuentes primarias consultadas:
- https://huggingface.co/Thermostatic/rosettia-quy-gspo-nllb13b-lora
- https://huggingface.co/Thermostatic/rosettia-quy-gspo-nllb13b-merged
- https://huggingface.co/Thermostatic/rosettia-quy-v30b-9b-merged
- https://huggingface.co/Thermostatic/rosettia-chanka-4b-alpha160
- https://huggingface.co/openai/whisper-large-v3-turbo
- https://opennmt.net/CTranslate2/quantization.html
- https://huggingface.co/Qwen/Qwen3.6-35B-A3B
- https://docs.ollama.com/capabilities/structured-outputs
