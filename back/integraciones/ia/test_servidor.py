import base64
import concurrent.futures
import hashlib
import json
from pathlib import Path
import tempfile
import threading
import unittest
import urllib.error
import urllib.request
from servidor import Api, Store


def solicitud(i, owner="usuario"):
    return {"id": hashlib.sha256(str(i).encode()).hexdigest(), "propietario": hashlib.sha256(owner.encode()).hexdigest(),
            "idiomaOrigen": "es", "idiomaDestino": "quy", "audioBase64": base64.b64encode(b"audio-de-prueba").decode()}


class StoreTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.path = Path(self.temp.name) / "voz.sqlite"
        self.store = Store(self.path)

    def tearDown(self):
        self.temp.cleanup()

    def test_idempotencia_y_conflicto(self):
        data = solicitud(1)
        self.assertEqual(202, self.store.submit(data)[0])
        self.assertEqual(202, self.store.submit(data)[0])
        data["audioBase64"] = base64.b64encode(b"otro-audio").decode()
        self.assertEqual(409, self.store.submit(data)[0])
        self.assertIsNotNone(self.store.claim())
        self.assertIsNone(self.store.claim())

    def test_reinicio_recupera_pendientes(self):
        data = solicitud(1)
        self.store.submit(data)
        self.store.claim()
        restarted = Store(self.path)
        self.assertEqual("pendiente", restarted.get(data["id"])["estado"])
        self.assertEqual(1, restarted.claim()["attempts"])
        restarted.finish(data["id"], {"traduccion": "rimay"})
        self.assertEqual("listo", restarted.get(data["id"])["estado"])
        with restarted.connect() as db:
            self.assertIsNone(db.execute("SELECT audio FROM jobs").fetchone()[0])

    def test_un_cliente_no_ocupa_toda_la_cola(self):
        self.assertEqual(202, self.store.submit(solicitud(1))[0])
        self.assertEqual(202, self.store.submit(solicitud(2))[0])
        self.assertEqual(429, self.store.submit(solicitud(3))[0])
        self.assertEqual(202, self.store.submit(solicitud(4, "otro"))[0])

    def test_carga_concurrente_respeta_limite_global(self):
        with concurrent.futures.ThreadPoolExecutor(max_workers=12) as pool:
            codes = list(pool.map(lambda i: self.store.submit(solicitud(i, str(i)))[0], range(30)))
        self.assertEqual(16, codes.count(202))
        self.assertEqual(14, codes.count(429))

    def test_valida_datos_y_audio(self):
        for change in [{"idiomaOrigen": "en"}, {"audioBase64": "???"}, {"audioBase64": ""}, {"id": "../escape"}]:
            with self.assertRaises(ValueError):
                self.store.submit({**solicitud(1), **change})

    def test_http_exige_clave_y_no_expone_trabajos(self):
        api = Api(("127.0.0.1", 0), self.store, "clave-prueba")
        thread = threading.Thread(target=api.serve_forever, daemon=True); thread.start()
        url = f"http://127.0.0.1:{api.server_port}"
        try:
            with self.assertRaises(urllib.error.HTTPError) as err:
                urllib.request.urlopen(url + "/health")
            self.assertEqual(401, err.exception.code)
            err.exception.close()
            request = urllib.request.Request(url + "/v1/fragmentos", json.dumps(solicitud(1)).encode(),
                                             {"X-Andina-Key": "clave-prueba", "Content-Type": "application/json"})
            with urllib.request.urlopen(request) as response:
                self.assertEqual(202, response.status)
            request = urllib.request.Request(url + "/v1/fragmentos/" + solicitud(1)["id"], headers={"X-Andina-Key": "clave-prueba"})
            with urllib.request.urlopen(request) as response:
                self.assertEqual("pendiente", json.load(response)["estado"])
        finally:
            api.shutdown(); api.server_close(); thread.join(timeout=2)


if __name__ == "__main__":
    unittest.main()
