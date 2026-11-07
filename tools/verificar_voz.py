#!/usr/bin/env python3
"""Prueba técnica con audio sintético; no mide calidad lingüística con humanos.

API_BASE=http://127.0.0.1:18083 ANDINA_TEST_PASSWORD=... python3 tools/verificar_voz.py audio.wav
No modifica las entidades escolares. Crea fragmentos temporales en la API de IA.
"""
import base64
import json
import os
from pathlib import Path
import sys
import time
import urllib.request
import uuid

BASE = os.environ.get("API_BASE", "http://127.0.0.1:18083").rstrip("/")
OUT = Path(os.environ.get("ANDINA_VOZ_REPORT", "/tmp/andina-voz-verificacion.json"))


def main():
    if len(sys.argv) != 2 or "ANDINA_TEST_PASSWORD" not in os.environ:
        raise SystemExit("Indica archivo WAV y ANDINA_TEST_PASSWORD de la BD ficticia de pruebas")
    token = ""
    def call(path, data=None):
        req = urllib.request.Request(BASE + path, json.dumps(data).encode() if data is not None else None,
                                     {"Content-Type": "application/json", **({"Authorization": "Bearer " + token} if token else {})})
        with urllib.request.urlopen(req, timeout=12) as response:
            return response.status, json.load(response)
    token = call("/login", {"idPersona": int(os.environ.get("ANDINA_TEST_PERSONA", "1")), "passwordPersona": os.environ["ANDINA_TEST_PASSWORD"]})[1]["token"]
    sesiones = call("/api/sesiones-virtuales/pagina?size=1")[1]["content"]
    if not sesiones:
        raise SystemExit("La BD de prueba necesita una sesión virtual")
    ruta = f"/api/sesiones-virtuales/{sesiones[0]['idSesionVirtual']}/videollamada"
    audio = Path(sys.argv[1]).read_bytes()
    report = {"tipo": "circuito técnico con audio sintético; sin evaluación de hablantes", "resultados": []}
    for source in ["es", "quy"]:
        job = str(uuid.uuid4())
        data = {"id": job, "idiomaOrigen": source, "audioBase64": base64.b64encode(audio).decode()}
        start = time.perf_counter()
        status, accepted = call(ruta + "/fragmentos", data)
        assert status == 202 and accepted["id"] == job
        enqueue_ms = int((time.perf_counter() - start) * 1000)
        assert call(ruta + "/fragmentos", data)[0] == 202  # reintento idempotente
        deadline = time.monotonic() + 600
        previous = ""
        while time.monotonic() < deadline:
            _, result = call(ruta + "/fragmentos/" + job)
            if result["estado"] != previous:
                print(source, result["estado"], flush=True); previous = result["estado"]
            if result["estado"] == "error":
                raise RuntimeError(result.get("mensaje", "Falló el procesamiento"))
            if result["estado"] == "listo":
                assert result["transcripcion"] and result["traduccion"], result
                audio = base64.b64decode(result["audioBase64"])
                assert audio[:4] == b"RIFF" and audio[8:12] == b"WAVE" and len(audio) > 1000
                row = {"origen": source, "destino": result["idiomaDestino"], "transcripcion": result["transcripcion"],
                       "traduccion": result["traduccion"], "audio_wav_bytes": len(audio),
                       "encolado_ms": enqueue_ms, "inferencia_ms": result["milisegundos"],
                       "total_ms": int((time.perf_counter()-start)*1000)}
                report["resultados"].append(row)
                print(json.dumps(row, ensure_ascii=False), flush=True)
                break
            time.sleep(1)
        else:
            raise TimeoutError("El servicio no completó el fragmento en diez minutos")
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n")
    print("Informe:", OUT)


if __name__ == "__main__":
    main()
