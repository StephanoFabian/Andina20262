import hashlib
import json
import math
from pathlib import Path
import tempfile
import unittest
import zipfile
import xml.etree.ElementTree as ET
from PIL import Image
from renderizar import geometry,renderizar
from servidor import Store


def request(i,owner='uno'):
    return {'id':hashlib.sha256(str(i).encode()).hexdigest(),'propietario':hashlib.sha256(owner.encode()).hexdigest(),
            'tema':'Triángulos','nivel':'Secundaria','instrucciones':'Ejemplo de Pitágoras'}


class MaterialTests(unittest.TestCase):
    def setUp(self):
        self.temp=tempfile.TemporaryDirectory();self.root=Path(self.temp.name);self.store=Store(self.root)
    def tearDown(self):self.temp.cleanup()

    def test_reintento_no_duplica_y_cambio_de_contenido_conflicta(self):
        data=request(1);self.assertEqual(202,self.store.submit(data)[0]);self.assertEqual(202,self.store.submit(data)[0])
        self.assertEqual(409,self.store.submit({**data,'tema':'Otro tema'})[0])
        self.assertIsNotNone(self.store.claim());self.assertIsNone(self.store.claim())

    def test_un_usuario_no_ocupa_la_cola_y_limite_global(self):
        self.assertEqual(202,self.store.submit(request(1))[0])
        self.assertEqual(429,self.store.submit(request(2))[0])
        for i in range(2,5):self.assertEqual(202,self.store.submit(request(i,str(i)))[0])
        self.assertEqual(429,self.store.submit(request(6,'otro'))[0])

    def test_reinicio_recupera_etapa_de_texto(self):
        self.store.submit(request(1));self.store.claim()
        recovered=Store(self.root)
        self.assertEqual('pendiente',recovered.get(request(1)['id'])['estado'])
        self.assertEqual(1,recovered.claim()['intentos_texto'])

    def test_reinicio_no_reescribe_texto_ya_generado(self):
        job=request(1)['id'];self.store.submit(request(1));self.store.claim()
        self.store.text_ready(job,'Ejemplo','Texto que debe conservarse')
        row=self.store.claim();self.assertEqual('texto_listo',row['estado'])
        recovered=Store(self.root)
        self.assertEqual('texto_listo',recovered.get(job)['estado'])
        self.assertEqual('Texto que debe conservarse',recovered.get(job)['texto'])

    def test_valida_tamanos_identidad_y_tema(self):
        for change in [{'id':'../fuera'},{'tema':''},{'tema':'A'*201},{'instrucciones':'A'*1001}]:
            with self.assertRaises(ValueError):self.store.submit({**request(1),**change})


class GeometryTests(unittest.TestCase):
    def test_pitagoras_area_y_perimetro_recalculados(self):
        data=geometry({'tipo':'triangulo_rectangulo','titulo':'Ejemplo','a':3,'b':4,'c':999})
        self.assertEqual(5,data['c']);self.assertAlmostEqual(6,data['area']);self.assertEqual(12,data['perimetro'])
    def test_heron_rectangulo_y_circulo(self):
        self.assertAlmostEqual(6,geometry({'tipo':'triangulo','titulo':'T','a':3,'b':4,'c':5})['area'])
        self.assertEqual(12,geometry({'tipo':'rectangulo','titulo':'R','a':3,'b':4})['area'])
        self.assertAlmostEqual(9*math.pi,geometry({'tipo':'circulo','titulo':'C','a':3})['area'])
    def test_rechaza_medidas_imposibles_y_no_finitas(self):
        for args in [{'a':1,'b':2,'c':10},{'a':float('nan'),'b':2,'c':3},{'a':True,'b':2,'c':3}]:
            with self.assertRaises(ValueError):geometry({'tipo':'triangulo','titulo':'Error',**args})
    def test_exporta_png_svg_y_pdf_con_texto_escapado(self):
        with tempfile.TemporaryDirectory() as temp:
            root=Path(temp);(root/'material.txt').write_text('Prueba')
            files=renderizar(root,'Triángulos','Ejemplo: catetos 3 y 4.',
                              [{'tipo':'triangulo_rectangulo','titulo':'<script>no se ejecuta</script>','a':3,'b':4,'c':0}],
                              'modelo-prueba-texto','modelo-prueba-figuras')
            self.assertIn('material.pdf',files);self.assertTrue((root/'material.pdf').read_bytes().startswith(b'%PDF'))
            with Image.open(root/'figura-1.png') as image:self.assertEqual((1100,700),image.size)
            tree=ET.parse(root/'figura-1.svg');self.assertFalse(any(node.tag.endswith('script') for node in tree.iter()))
            data=json.loads((root/'geometria-verificada.json').read_text());self.assertEqual(5,data[0]['c'])
            if 'material.docx' in files:
                with zipfile.ZipFile(root/'material.docx') as word:
                    self.assertTrue(any(name.startswith('word/media/') for name in word.namelist()))
                    self.assertNotIn('TargetMode="External"',word.read('word/_rels/document.xml.rels').decode())


if __name__=='__main__':unittest.main()
