#!/usr/bin/env python3
"""Preparación y ejecución independientes de LiveKit, IA y Spring local."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import secrets
import shutil
import subprocess
import sys
import tarfile
import urllib.request

ROOT = Path(__file__).resolve().parent
STATE = ROOT / ".local"
REPO = ROOT.parent
LIVEKIT_VERSION = "1.13.11"


def settings():
    STATE.mkdir(exist_ok=True, mode=0o700)
    file = STATE / "config.json"
    if not file.exists():
        config = {
            "ANDINA_VIDEO_URL": "ws://localhost:7880",
            "ANDINA_VIDEO_API_KEY": "andina-local",
            "ANDINA_VIDEO_API_SECRET": secrets.token_urlsafe(48),
            "ANDINA_IA_API_KEY": secrets.token_urlsafe(48),
            "ANDINA_IA_URL": "http://127.0.0.1:8095",
            "ANDINA_IA_THREADS": "6",
        }
        file.write_text(json.dumps(config, indent=2) + "\n"); file.chmod(0o600)
    config = json.loads(file.read_text())
    if "ANDINA_MATERIAL_IA_API_KEY" not in config:
        config["ANDINA_MATERIAL_IA_API_KEY"] = secrets.token_urlsafe(48)
        config["ANDINA_MATERIAL_IA_URL"] = "http://127.0.0.1:8096"
        config["ANDINA_MATERIAL_MODEL"] = "llama3.3:70b-1bit"
        config["ANDINA_MATERIAL_FIGURE_MODEL"] = "qwen3:14b"
        file.write_text(json.dumps(config, indent=2)+"\n"); file.chmod(0o600)
    return config


def python_ia():
    selected = os.environ.get("ANDINA_IA_PYTHON")
    existing = Path("/mnt/datos/Herramientas/voz_chatterbox/.venv/bin/python")
    return selected or (str(existing) if existing.exists() else sys.executable)


def env():
    current = {**settings(), **os.environ}
    current["PYTHONPATH"] = str(ROOT / "ia" / ".deps") + os.pathsep + current.get("PYTHONPATH", "")
    current.setdefault("ANDINA_IA_STATE", str(STATE / "voz.sqlite"))
    current.setdefault("HF_HUB_DISABLE_PROGRESS_BARS", "1")
    return current


def prepare():
    config = settings()
    binary = STATE / "livekit-server"
    if not binary.exists():
        url = f"https://api.github.com/repos/livekit/livekit/releases/tags/v{LIVEKIT_VERSION}"
        with urllib.request.urlopen(url, timeout=30) as r:
            release = json.load(r)
        name = f"livekit_{LIVEKIT_VERSION}_linux_amd64.tar.gz"
        asset = next(a for a in release["assets"] if a["name"] == name)
        archive = STATE / name
        print("Descargando LiveKit oficial", LIVEKIT_VERSION, flush=True)
        with urllib.request.urlopen(asset["browser_download_url"], timeout=60) as source, archive.open("wb") as out:
            shutil.copyfileobj(source, out)
        digest = "sha256:" + hashlib.sha256(archive.read_bytes()).hexdigest()
        if asset.get("digest") and digest != asset["digest"]:
            raise RuntimeError("La descarga de LiveKit no coincide con su digest oficial")
        with tarfile.open(archive) as tar:
            member = next(m for m in tar.getmembers() if m.name.rsplit("/", 1)[-1] == "livekit-server" and m.isfile())
            with tar.extractfile(member) as source, binary.open("wb") as out:
                shutil.copyfileobj(source, out)
        binary.chmod(0o700)
        (STATE / "livekit-version.json").write_text(json.dumps({"version": LIVEKIT_VERSION, "digest": digest, "verificado": bool(asset.get("digest"))}))
        archive.unlink()
    yaml = STATE / "livekit.yaml"
    yaml.write_text("port: 7880\nbind_addresses: [127.0.0.1]\nrtc:\n  node_ip: 127.0.0.1\n  tcp_port: 7881\n  udp_port: 7882\n  use_external_ip: false\nroom:\n  max_participants: 20\nkeys:\n  " + config["ANDINA_VIDEO_API_KEY"] + ": " + config["ANDINA_VIDEO_API_SECRET"] + "\n")
    yaml.chmod(0o600)
    deps = ROOT / "ia" / ".deps"
    # Reutiliza Torch/ROCm y Transformers instalados. Los paquetes que faltan
    # quedan en una carpeta privada de Andina; no cambia el entorno de voz.
    check = subprocess.check_output([python_ia(), "-c", "import importlib.util,json; print(json.dumps([p for p in ['sentencepiece','uroman'] if importlib.util.find_spec(p) is None]))"], env=env(), text=True)
    missing = json.loads(check)
    if missing:
        versions = {"sentencepiece": "sentencepiece==0.2.3", "uroman": "uroman==1.3.1.1"}
        uv = shutil.which("uv")
        if uv:
            cmd = [uv, "pip", "install", "--python", python_ia(), "--target", str(deps)]
        else:
            cmd = [python_ia(), "-m", "pip", "install", "--target", str(deps)]
        subprocess.run(cmd + [versions[p] for p in missing], check=True)
    subprocess.run([python_ia(), str(ROOT / "ia" / "descargar_modelos.py")], env=env(), check=True)
    print("Preparación completa. Claves locales guardadas con permisos 600.")


def run(which):
    current = env()
    if which == "video":
        cmd = [str(STATE / "livekit-server"), "--config", str(STATE / "livekit.yaml")]
    elif which == "ia":
        current["HF_HUB_OFFLINE"] = "1"
        current["TRANSFORMERS_OFFLINE"] = "1"
        cmd = [python_ia(), str(ROOT / "ia" / "servidor.py")]
    elif which == "materiales":
        cmd = [os.environ.get("ANDINA_MATERIAL_PYTHON", sys.executable), str(ROOT / "materiales" / "servidor.py")]
    else:
        current.setdefault("SERVER_ADDRESS", "127.0.0.1")
        java = shutil.which("java")
        configured = os.environ.get("JAVA_HOME")
        local_java = Path("/home/nathaniel-r/.jdks/ms-21.0.12.1/bin/java")
        if os.environ.get("ANDINA_JAVA"):
            java = os.environ["ANDINA_JAVA"]
        elif local_java.exists():
            java = str(local_java)
        elif configured:
            java = str(Path(configured) / "bin" / "java")
        jar = REPO / "target" / "demoSM2-0.0.1-SNAPSHOT.jar"
        if not jar.exists():
            raise SystemExit("Compila Spring primero: bash ./mvnw -DskipTests package")
        cmd = [java, "-jar", str(jar)]
    os.chdir(REPO)
    os.execvpe(cmd[0], cmd, current)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("accion", choices=["preparar", "video", "ia", "materiales", "backend"])
    args = parser.parse_args()
    if args.accion == "preparar":
        prepare()
    else:
        run(args.accion)
