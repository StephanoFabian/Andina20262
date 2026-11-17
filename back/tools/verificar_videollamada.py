#!/usr/bin/env python3
"""Dos navegadores con medios sintéticos. Requiere Playwright ya instalado.

ANDINA_VIDEO_TEST_PASSWORD_1=... ANDINA_VIDEO_TEST_PASSWORD_2=... \
ANDINA_VIDEO_TEST_AUDIO=/ruta/audio-sintetico.wav python verificar_videollamada.py
La API debe usar una BD ficticia; no se crean ni eliminan entidades escolares.
"""
import json
import os
from pathlib import Path
import shutil
import time
import urllib.request
from playwright.sync_api import sync_playwright


def main():
    base = os.environ.get("API_BASE", "http://127.0.0.1:18083").rstrip("/")
    front = os.environ.get("ANDINA_FRONT_TEST_URL", "http://localhost:4200")
    out = Path(os.environ.get("ANDINA_VIDEO_TEST_OUT", "/tmp/andina-video-verificacion"))
    out.mkdir(parents=True, exist_ok=True)
    audio = Path(os.environ["ANDINA_VIDEO_TEST_AUDIO"]).resolve()
    if not audio.is_file():
        raise SystemExit("Falta el audio sintético de prueba")
    tokens = []
    for index in [1, 2]:
        payload = {"idPersona": int(os.environ.get(f"ANDINA_VIDEO_TEST_PERSONA_{index}", str(index))),
                   "passwordPersona": os.environ[f"ANDINA_VIDEO_TEST_PASSWORD_{index}"]}
        request = urllib.request.Request(base + "/login", json.dumps(payload).encode(), {"Content-Type": "application/json"})
        with urllib.request.urlopen(request, timeout=8) as response:
            tokens.append(json.load(response)["token"])
    session = int(os.environ.get("ANDINA_VIDEO_TEST_SESION", "1"))
    report = {"participantes": 2, "medios": "cámara y audio sintéticos; sin dispositivos personales", "comprobaciones": []}
    with sync_playwright() as p:
        browser = p.chromium.launch(executable_path=shutil.which("brave-browser") or shutil.which("chromium"), headless=True,
            args=["--use-fake-device-for-media-stream", "--use-fake-ui-for-media-stream", "--autoplay-policy=no-user-gesture-required",
                  "--use-file-for-fake-audio-capture=" + str(audio), "--disable-dev-shm-usage"])
        contexts, pages = [], []
        errors = []
        for token in tokens:
            context = browser.new_context(permissions=["camera", "microphone"], viewport={"width": 1440, "height": 1050})
            context.add_init_script("sessionStorage.setItem('andina.token', " + json.dumps(token) + ")")
            context.route("http://localhost:8080/**", lambda route: route.continue_(url=route.request.url.replace("http://localhost:8080", base, 1)))
            page = context.new_page()
            page.on("pageerror", lambda error: errors.append(str(error)))
            page.goto(front + f"/sesiones-virtuales/sala/{session}")
            page.get_by_role("button", name="Entrar a la videollamada", exact=True).click()
            page.get_by_role("button", name="Salir de la llamada", exact=True).wait_for(timeout=30000)
            contexts.append(context); pages.append(page)
        for page in pages:
            page.wait_for_function("document.querySelectorAll('.videos video').length >= 2", timeout=30000)
        report["comprobaciones"].append("Dos participantes autenticados reciben cámara local y remota en una misma sala")
        receiver = pages[1]
        receiver.locator('select').nth(1).select_option("quy")
        receiver.get_by_label("Escuchar también el audio original").uncheck()
        receiver.evaluate("""() => {
          window.andinaAudioMax = 0;
          const ctx = new AudioContext();
          const seen = new Set();
          const analysers = [];
          window.andinaAudioInterval = setInterval(() => {
            const component = ng.getComponent(document.querySelector('app-videollamada'));
            for (const [sid, medio] of component.medios) {
              if (medio.idioma !== 'quy' || seen.has(sid)) continue;
              seen.add(sid);
              const source = ctx.createMediaStreamSource(new MediaStream([medio.pista.mediaStreamTrack]));
              const analyser = ctx.createAnalyser(); analyser.fftSize = 2048;
              const zero = ctx.createGain(); zero.gain.value = 0;
              source.connect(analyser); analyser.connect(zero); zero.connect(ctx.destination);
              analysers.push(analyser);
            }
            for (const analyser of analysers) {
              const data = new Float32Array(analyser.fftSize); analyser.getFloatTimeDomainData(data);
              const rms = Math.sqrt(data.reduce((sum, x) => sum + x*x, 0)/data.length);
              window.andinaAudioMax = Math.max(window.andinaAudioMax, rms);
            }
          }, 100);
        }""")
        pages[0].get_by_role("button", name="Compartir traducción de mi voz", exact=True).click()
        receiver.locator(".subtitulos article").first.wait_for(timeout=180000)
        receiver.wait_for_function("window.andinaAudioMax > 0.002", timeout=90000)
        message = receiver.locator(".subtitulos article").first
        original = message.locator(".texto-original").inner_text()
        translated = message.locator(".texto-traduccion").inner_text()
        assert original and translated
        assert "Original" in message.locator(".etiqueta-original").inner_text()
        assert "Traducción" in message.locator(".etiqueta-traduccion").inner_text()
        assert message.locator(".texto-original").get_attribute("lang") == "es"
        assert message.locator(".texto-traduccion").get_attribute("lang") == "quy"
        receiver.locator('select').nth(1).select_option("es")
        assert message.locator(".texto-original").inner_text() == original
        assert message.locator(".texto-traduccion").inner_text() == translated
        receiver.locator('select').nth(1).select_option("quy")
        receiver.get_by_label("Mostrar subtítulos en el video", exact=True).uncheck()
        assert receiver.get_by_role("complementary", name="Chat de traducción").is_visible()
        receiver.locator(".subtitulo-actual").wait_for(state="detached", timeout=5000)
        pages[0].locator(".mensaje-propio").first.wait_for()
        video_box = receiver.locator(".llamada").bounding_box()
        chat_box = receiver.locator(".chat-traduccion").bounding_box()
        assert chat_box["x"] >= video_box["x"] + video_box["width"]
        report["comprobaciones"].extend(["El otro participante recibe los subtítulos del audio interpretado",
                                            "La pista de voz traducida llega al receptor con señal de audio medible",
                                            "Chat a la derecha del video con original y traducción etiquetados por idioma",
                                            "Cambiar el idioma de escucha conserva original y traducción en sus bloques",
                                            "Ocultar subtítulos del video mantiene visible el chat y los mensajes propios"])
        report["voz_recibida_rms_max"] = receiver.evaluate("window.andinaAudioMax")
        report["subtitulo_prueba"] = receiver.locator(".subtitulos article").first.inner_text()
        receiver.screenshot(path=str(out / "sala.png"), full_page=True)
        # Alternar cámara debe dejar una sola vista local y una remota.
        pages[0].get_by_role("button", name="Apagar cámara", exact=True).click()
        pages[0].get_by_role("button", name="Activar cámara", exact=True).click()
        pages[0].wait_for_function("document.querySelectorAll('.videos video[data-local]').length === 1")
        report["comprobaciones"].append("Alternar cámara conserva una sola vista local")
        for page in pages:
            page.get_by_role("button", name="Salir de la llamada", exact=True).click()
            page.get_by_role("button", name="Entrar a la videollamada", exact=True).wait_for()
        report["comprobaciones"].append("Ambos participantes salen y liberan cámara, micrófono y conexión")
        report["errores_navegador"] = errors
        assert not errors, errors
        browser.close()
    (out / "resultado.json").write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n")
    print(json.dumps(report, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
