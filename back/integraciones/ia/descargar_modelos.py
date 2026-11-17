"""Descarga solo los pesos, tokenizadores y dos adaptadores necesarios."""
import json
from pathlib import Path
from huggingface_hub import hf_hub_download, list_repo_files

if __name__ == "__main__":
    manifest = json.loads((Path(__file__).parent / "modelos.json").read_text())
    for name, model in manifest.items():
        print("Preparando", name, model["repo"], model["revision"], flush=True)
        files = set(list_repo_files(model["repo"], revision=model["revision"]))
        wanted = {"config.json", "preprocessor_config.json", "tokenizer_config.json", "special_tokens_map.json",
                  "tokenizer.json", "vocab.json", "added_tokens.json", "sentencepiece.bpe.model", "generation_config.json"}
        wanted.add("model.safetensors" if "model.safetensors" in files else "pytorch_model.bin")
        if name == "asr":
            wanted.update({"adapter.spa.safetensors", "adapter.quy.safetensors"})
        for file in sorted(wanted & files):
            hf_hub_download(model["repo"], file, revision=model["revision"])
    print("Pesos y tokenizadores disponibles en la caché local.", flush=True)
