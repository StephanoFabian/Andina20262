#!/usr/bin/env python3
"""Verifica un material ya generado en una BD ficticia, sin iniciar otro modelo.

Requiere Playwright, ANDINA_MATERIAL_TEST_PASSWORD y ANDINA_MATERIAL_TEST_ID.
No usa credenciales ni sesiones del navegador personal.
"""
import json
import os
from pathlib import Path
import shutil
import urllib.request
import zipfile
from playwright.sync_api import sync_playwright


def main():
    api = os.environ.get('API_BASE', 'http://127.0.0.1:18083').rstrip('/')
    front = os.environ.get('ANDINA_FRONT_TEST_URL', 'http://localhost:4200').rstrip('/')
    job = os.environ['ANDINA_MATERIAL_TEST_ID']
    out = Path(os.environ.get('ANDINA_MATERIAL_TEST_OUT', '/tmp/andina-material-verificacion'))
    out.mkdir(parents=True, exist_ok=True)
    payload = {'idPersona': int(os.environ.get('ANDINA_MATERIAL_TEST_PERSONA', '1')),
               'passwordPersona': os.environ['ANDINA_MATERIAL_TEST_PASSWORD']}
    request = urllib.request.Request(api + '/login', json.dumps(payload).encode(), {'Content-Type': 'application/json'})
    with urllib.request.urlopen(request, timeout=10) as response:
        token = json.load(response)['token']
    report = {'comprobaciones': [], 'errores_navegador': []}
    with sync_playwright() as p:
        browser = p.chromium.launch(executable_path=shutil.which('brave-browser') or shutil.which('chromium'),
                                    headless=True, args=['--disable-dev-shm-usage'])
        context = browser.new_context(viewport={'width': 1440, 'height': 1050}, accept_downloads=True)
        context.add_init_script("sessionStorage.setItem('andina.token', " + json.dumps(token) + ")")
        context.route('http://localhost:8080/**', lambda route: route.continue_(
            url=route.request.url.replace('http://localhost:8080', api, 1)))
        page = context.new_page()
        page.on('pageerror', lambda error: report['errores_navegador'].append(str(error)))
        page.goto(front + '/materiales')
        page.get_by_role('link', name='Crear con IA', exact=True).click()
        page.get_by_role('heading', name='Crear material con IA', exact=True).wait_for()
        report['comprobaciones'].append('Materiales ofrece el acceso Crear con IA a un usuario autorizado')
        page.goto(front + '/materiales/crear-con-ia?trabajo=' + job)
        page.get_by_role('heading', name='Material listo para descargar', exact=True).wait_for(timeout=30000)
        drafts = page.locator('.borrador .contenido')
        assert drafts.count() == 2 and all(drafts.nth(i).inner_text().strip() for i in (0, 1))
        report['comprobaciones'].append('Muestra por separado el texto original del 70B y la revisión usada para el PDF')
        page.locator('.figura img').first.wait_for()
        page.wait_for_function("Array.from(document.querySelectorAll('.figura img')).every(i => i.complete && i.naturalWidth > 0)")
        report['comprobaciones'].append('Las imágenes geométricas autenticadas se muestran correctamente')
        for label, filename, signature in [
            ('Descargar PDF', 'material.pdf', b'%PDF'),
            ('Descargar Word', 'material.docx', b'PK'),
            ('Descargar texto original', 'material.txt', None),
            ('Descargar texto revisado', 'material-revisado.txt', None),
            ('Descargar imagen PNG', 'figura-1.png', b'\x89PNG'),
            ('Descargar SVG', 'figura-1.svg', b'<svg'),
        ]:
            with page.expect_download() as event:
                page.get_by_role('button', name=label, exact=True).first.click()
            download = event.value
            assert download.suggested_filename == filename
            download.save_as(str(out / filename))
            data = (out / filename).read_bytes()
            assert data and (signature is None or data.startswith(signature))
            if filename.endswith('.docx'):
                with zipfile.ZipFile(out / filename) as word:
                    assert any(name.startswith('word/media/') for name in word.namelist())
                    assert 'TargetMode="External"' not in word.read('word/_rels/document.xml.rels').decode()
            if signature is None:
                assert drafts.nth(1 if 'revisado' in filename else 0).inner_text() in data.decode('utf-8')
        report['comprobaciones'].append('PDF, Word, texto original, texto revisado, PNG y SVG se descargan con contenido válido')
        page.reload()
        page.get_by_role('heading', name='Material listo para descargar', exact=True).wait_for()
        assert page.locator('.borrador .contenido').count() == 2
        report['comprobaciones'].append('Recargar recupera el mismo trabajo sin generar otro material')
        page.screenshot(path=str(out / 'material-escritorio.png'), full_page=True)
        page.set_viewport_size({'width': 390, 'height': 844})
        page.screenshot(path=str(out / 'material-movil.png'), full_page=True)
        assert page.evaluate('document.documentElement.scrollWidth <= innerWidth + 1')
        report['comprobaciones'].append('La vista de 390 píxeles permite leer el material sin desbordamiento horizontal')
        assert not report['errores_navegador'], report['errores_navegador']
        browser.close()
    (out / 'resultado.json').write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n')
    print(json.dumps(report, ensure_ascii=False, indent=2))


if __name__ == '__main__':
    main()
