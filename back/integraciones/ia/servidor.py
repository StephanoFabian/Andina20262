#!/usr/bin/env python3
"""API local de voz. SQLite conserva los fragmentos pendientes tras reinicios.

Solo Spring conoce la clave. Un trabajador procesa modelos; las peticiones HTTP
no esperan inferencia. No se usa una transcripción o traducción simulada.
"""
import base64
from contextlib import contextmanager
import hashlib
import hmac
import io
import json
import logging
import os
from pathlib import Path
import re
import sqlite3
import subprocess
import threading
import time
import wave
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

LOG = logging.getLogger("andina-voz")
MAX_AUDIO = 1_000_000
MAX_BODY = 1_410_000
LANGS = {"es": "spa", "quy": "quy"}


class Store:
    def __init__(self, path):
        self.path = str(path)
        Path(path).parent.mkdir(parents=True, exist_ok=True)
        with self.connect() as db:
            db.execute("PRAGMA journal_mode=WAL")
            db.execute("""CREATE TABLE IF NOT EXISTS jobs (
                id TEXT PRIMARY KEY, owner TEXT NOT NULL, fingerprint TEXT NOT NULL,
                source TEXT NOT NULL, target TEXT NOT NULL, audio BLOB,
                state TEXT NOT NULL, result TEXT, created REAL NOT NULL,
                attempts INTEGER NOT NULL DEFAULT 0)""")
            db.execute("CREATE INDEX IF NOT EXISTS ix_jobs_state_created ON jobs(state, created)")
            db.execute("CREATE INDEX IF NOT EXISTS ix_jobs_owner_state ON jobs(owner, state)")
            db.execute("UPDATE jobs SET state='pendiente' WHERE state='procesando'")

    @contextmanager
    def connect(self):
        db = sqlite3.connect(self.path, timeout=3)
        db.row_factory = sqlite3.Row
        db.execute("PRAGMA busy_timeout=3000")
        try:
            with db:
                yield db
        finally:
            db.close()

    def submit(self, data):
        job, owner = data.get("id", ""), data.get("propietario", "")
        source, target = data.get("idiomaOrigen"), data.get("idiomaDestino")
        if not re.fullmatch(r"[a-f0-9]{64}", job) or not re.fullmatch(r"[a-f0-9]{64}", owner):
            raise ValueError("Identificador inválido")
        if source not in LANGS or target not in LANGS or source == target:
            raise ValueError("Idiomas inválidos")
        encoded = data.get("audioBase64")
        if not isinstance(encoded, str) or len(encoded) > 1_400_000:
            raise ValueError("Audio demasiado grande")
        try:
            audio = base64.b64decode(encoded, validate=True)
        except (ValueError, TypeError) as ex:
            raise ValueError("Audio inválido") from ex
        if not audio or len(audio) > MAX_AUDIO:
            raise ValueError("Audio inválido")
        digest = hashlib.sha256(source.encode() + target.encode() + audio).hexdigest()
        with self.connect() as db:
            db.execute("BEGIN IMMEDIATE")
            old = db.execute("SELECT fingerprint FROM jobs WHERE id=?", (job,)).fetchone()
            if old:
                if old["fingerprint"] != digest:
                    return 409, {"mensaje": "Este identificador ya tiene otro audio"}
                return 202, self.get(job, db)
            # Borrado por lotes; conserva hasta 20 minutos los resultados y errores.
            db.execute("""DELETE FROM jobs WHERE id IN (SELECT id FROM jobs
                WHERE state IN ('listo','error') AND created < ? ORDER BY created LIMIT 64)""",
                       (time.time() - 1200,))
            pending = db.execute("SELECT count(*) FROM jobs WHERE state IN ('pendiente','procesando')").fetchone()[0]
            own = db.execute("SELECT count(*) FROM jobs WHERE owner=? AND state IN ('pendiente','procesando')", (owner,)).fetchone()[0]
            count = db.execute("SELECT count(*) FROM jobs").fetchone()[0]
            if pending >= 16 or own >= 2 or count >= 256:
                return 429, {"mensaje": "Servicio ocupado; espera el fragmento anterior"}
            db.execute("INSERT INTO jobs(id,owner,fingerprint,source,target,audio,state,created) VALUES(?,?,?,?,?,?,'pendiente',?)",
                       (job, owner, digest, source, target, audio, time.time()))
        return 202, {"id": job, "estado": "pendiente", "idiomaOrigen": source, "idiomaDestino": target}

    def get(self, job, db=None):
        if db is None:
            with self.connect() as conn:
                return self.get(job, conn)
        row = db.execute("SELECT id,state,source,target,result FROM jobs WHERE id=?", (job,)).fetchone()
        if row is None:
            return None
        result = json.loads(row["result"]) if row["result"] else {}
        return {**result, "id": row["id"], "estado": row["state"], "idiomaOrigen": row["source"], "idiomaDestino": row["target"]}

    def claim(self):
        with self.connect() as db:
            db.execute("BEGIN IMMEDIATE")
            row = db.execute("SELECT * FROM jobs WHERE state='pendiente' ORDER BY created LIMIT 1").fetchone()
            if row:
                db.execute("UPDATE jobs SET state='procesando', attempts=attempts+1 WHERE id=?", (row["id"],))
            return row

    def finish(self, job, result, state="listo"):
        with self.connect() as db:
            db.execute("UPDATE jobs SET state=?, result=?, audio=NULL WHERE id=?",
                       (state, json.dumps(result, ensure_ascii=False), job))


class Engine:
    """Todos los componentes usan quechua de Ayacucho, código quy."""
    def __init__(self):
        import numpy as np
        import torch
        from transformers import AutoProcessor, Wav2Vec2ForCTC, AutoTokenizer, AutoModelForSeq2SeqLM, VitsModel
        self.np, self.torch = np, torch
        configured_device = os.environ.get("ANDINA_IA_DEVICE", "cpu")
        self.device = "cuda" if configured_device == "cuda" and torch.cuda.is_available() else "cpu"
        LOG.info("Dispositivo de inferencia: %s", self.device)
        torch.set_num_threads(int(os.environ.get("ANDINA_IA_THREADS", "6")))
        self.tokenizer_class, self.tts_class = AutoTokenizer, VitsModel
        self.manifest = json.loads((Path(__file__).parent / "modelos.json").read_text())
        from huggingface_hub import snapshot_download
        def args(name):
            item = self.manifest[name]
            snapshot = snapshot_download(item["repo"], revision=item["revision"], local_files_only=True)
            return {"pretrained_model_name_or_path": snapshot, "local_files_only": True}
        self.args = args
        LOG.info("Cargando reconocimiento de voz y traducción")
        self.asr_processor = AutoProcessor.from_pretrained(**args("asr"))
        self.asr = Wav2Vec2ForCTC.from_pretrained(**args("asr")).to(self.device).eval()
        self.asr_language = "eng"
        self.mt_tokenizer = AutoTokenizer.from_pretrained(**args("traduccion"), src_lang="spa_Latn")
        self.mt = AutoModelForSeq2SeqLM.from_pretrained(**args("traduccion")).to(device=self.device, dtype=torch.float16 if self.device == "cuda" else torch.float32).eval()
        # VITS resulta más rápido en CPU en el equipo medido; ASR y MT pueden
        # seguir en GPU. Conserva una selección independiente para otros equipos.
        self.tts_device = os.environ.get("ANDINA_TTS_DEVICE", "cpu")
        self.tts = {}
        LOG.info("Modelos de reconocimiento y traducción disponibles")

    def synthesize(self, text, language):
        if language not in self.tts:
            args = self.args("voz_" + language)
            self.tts[language] = (self.tokenizer_class.from_pretrained(**args), self.tts_class.from_pretrained(**args).to(self.tts_device).eval())
        tokenizer, model = self.tts[language]
        inputs = tokenizer(text.lower(), return_tensors="pt").to(self.tts_device)
        with self.torch.inference_mode():
            samples = model(**inputs).waveform[0].cpu().numpy()
        if len(samples) > model.config.sampling_rate * 24:
            raise ValueError("La voz traducida supera el límite de 24 segundos")
        pcm = (self.np.clip(samples, -1, 1) * 32767).astype("<i2").tobytes()
        out = io.BytesIO()
        with wave.open(out, "wb") as wav:
            wav.setnchannels(1); wav.setsampwidth(2); wav.setframerate(model.config.sampling_rate)
            wav.writeframes(pcm)
        return out.getvalue()

    def process(self, raw, source, target):
        start = time.perf_counter()
        decoded = subprocess.run(["ffmpeg", "-v", "error", "-nostdin", "-i", "pipe:0", "-t", "8",
                                  "-f", "f32le", "-ar", "16000", "-ac", "1", "pipe:1"],
                                 input=raw, capture_output=True, timeout=12, check=True).stdout
        samples = self.np.frombuffer(decoded, dtype="<f4").copy()
        if len(samples) < 1600 or float(self.np.sqrt(self.np.mean(samples * samples))) < 0.008:
            return {"transcripcion": "", "traduccion": "", "audioBase64": "", "milisegundos": int((time.perf_counter()-start)*1000)}
        decoded_at = time.perf_counter()
        lang = LANGS[source]
        if self.asr_language != lang:
            self.asr_processor.tokenizer.set_target_lang(lang)
            self.asr.load_adapter(lang, local_files_only=True)
            self.asr.to(self.device)
            self.asr_language = lang
        inputs = self.asr_processor(samples, sampling_rate=16000, return_tensors="pt").to(self.device)
        with self.torch.inference_mode():
            ids = self.asr(**inputs).logits.argmax(dim=-1)
        original = self.asr_processor.batch_decode(ids)[0].strip()[:600]
        if not original:
            return {"transcripcion": "", "traduccion": "", "audioBase64": "", "milisegundos": int((time.perf_counter()-start)*1000)}
        asr_at = time.perf_counter()
        self.mt_tokenizer.src_lang = "spa_Latn" if source == "es" else "quy_Latn"
        target_code = "spa_Latn" if target == "es" else "quy_Latn"
        encoded = self.mt_tokenizer(original, return_tensors="pt", truncation=True, max_length=256).to(self.device)
        with self.torch.inference_mode():
            tokens = self.mt.generate(**encoded, forced_bos_token_id=self.mt_tokenizer.convert_tokens_to_ids(target_code),
                                      max_new_tokens=180, num_beams=2)
        translated = self.mt_tokenizer.batch_decode(tokens, skip_special_tokens=True)[0].strip()[:900]
        translated_at = time.perf_counter()
        result = {"transcripcion": original, "traduccion": translated}
        try:
            result["audioBase64"] = base64.b64encode(self.synthesize(translated, target)).decode()
        except Exception:
            LOG.exception("No se pudo sintetizar el fragmento")
            result.update(audioBase64="", mensaje="Subtítulos disponibles; no se pudo generar la voz de este fragmento")
        finished = time.perf_counter()
        result["milisegundos"] = int((finished-start)*1000)
        LOG.info("Tiempos ms: decodificar=%d ASR=%d traducir=%d voz=%d",
                 (decoded_at-start)*1000, (asr_at-decoded_at)*1000,
                 (translated_at-asr_at)*1000, (finished-translated_at)*1000)
        return result


def work(store, stop):
    engine = None
    while not stop.is_set():
        row = store.claim()
        if row is None:
            stop.wait(0.4)
            continue
        try:
            if row["attempts"] >= 2:
                raise RuntimeError("Se agotaron los reintentos tras reiniciar")
            if engine is None:
                engine = Engine()
            result = engine.process(row["audio"], row["source"], row["target"])
            store.finish(row["id"], result)
        except Exception:
            LOG.exception("Error al procesar fragmento %s", row["id"][:8])
            store.finish(row["id"], {"mensaje": "No se pudo interpretar este fragmento. Revisa el servicio local y vuelve a hablar."}, "error")


class Api(ThreadingHTTPServer):
    daemon_threads = True
    request_queue_size = 8
    def __init__(self, address, store, key):
        super().__init__(address, Handler)
        self.store, self.key = store, key
        self.slots = threading.BoundedSemaphore(8)

    def process_request(self, request, address):
        if not self.slots.acquire(blocking=False):
            try:
                request.sendall(b"HTTP/1.1 503 Service Unavailable\r\nContent-Length: 0\r\nConnection: close\r\n\r\n")
            finally:
                self.shutdown_request(request)
            return
        try:
            super().process_request(request, address)
        except Exception:
            self.slots.release()
            raise

    def process_request_thread(self, request, address):
        try:
            request.settimeout(8)
            super().process_request_thread(request, address)
        finally:
            self.slots.release()


class Handler(BaseHTTPRequestHandler):
    def log_message(self, fmt, *args):
        pass  # No se guardan audio, credenciales ni transcripciones en los logs HTTP.

    def send_json(self, code, body):
        raw = json.dumps(body, ensure_ascii=False).encode()
        self.send_response(code)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(raw)))
        self.send_header("Cache-Control", "no-store")
        self.send_header("Connection", "close")
        self.end_headers()
        self.wfile.write(raw)

    def authorized(self):
        if not hmac.compare_digest(self.headers.get("X-Andina-Key", ""), self.server.key):
            self.send_json(401, {"mensaje": "No autorizado"})
            return False
        return True

    def do_GET(self):
        if not self.authorized():
            return
        if self.path == "/health":
            return self.send_json(200, {"estado": "disponible", "variedad": "Quechua de Ayacucho", "trabajadores": 1})
        match = re.fullmatch(r"/v1/fragmentos/([a-f0-9]{64})", self.path)
        if not match:
            return self.send_json(404, {"mensaje": "No encontrado"})
        result = self.server.store.get(match[1])
        self.send_json(200 if result else 404, result or {"mensaje": "No encontrado"})

    def do_POST(self):
        if not self.authorized():
            return
        if self.path != "/v1/fragmentos":
            return self.send_json(404, {"mensaje": "No encontrado"})
        try:
            length = int(self.headers.get("Content-Length", "0"))
            if length < 1 or length > MAX_BODY or self.headers.get("Transfer-Encoding"):
                return self.send_json(413, {"mensaje": "Cuerpo demasiado grande"})
            data = json.loads(self.rfile.read(length))
            if not isinstance(data, dict):
                raise ValueError("JSON inválido")
            code, result = self.server.store.submit(data)
            self.send_json(code, result)
        except (ValueError, TypeError):
            self.send_json(400, {"mensaje": "Audio o solicitud inválidos"})
        except sqlite3.OperationalError:
            self.send_json(503, {"mensaje": "Servicio ocupado"})


def main():
    key = os.environ.get("ANDINA_IA_API_KEY", "")
    if len(key) < 32:
        raise SystemExit("Configura ANDINA_IA_API_KEY con al menos 32 caracteres")
    logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
    state = Path(os.environ.get("ANDINA_IA_STATE", str(Path(__file__).parent.parent / ".local" / "voz.sqlite")))
    os.umask(0o077)
    store = Store(state)
    stop = threading.Event()
    worker = threading.Thread(target=work, args=(store, stop), daemon=True)
    worker.start()
    api = Api(("127.0.0.1", int(os.environ.get("ANDINA_IA_PORT", "8095"))), store, key)
    LOG.info("API local de interpretación lista en 127.0.0.1:%s", api.server_port)
    try:
        api.serve_forever()
    except KeyboardInterrupt:
        pass
    finally:
        stop.set(); api.server_close()


if __name__ == "__main__":
    main()
