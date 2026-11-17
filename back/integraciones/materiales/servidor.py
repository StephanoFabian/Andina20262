#!/usr/bin/env python3
"""Llama 70B redacta; una segunda IA propone figuras; herramientas locales exportan.

Los modelos reciben texto y esquemas JSON, nunca acceso al shell ni rutas de
archivos. Una cola independiente conserva las dos etapas sin bloquear voz.
"""
from contextlib import contextmanager
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
import hashlib
import hmac
import json
import logging
import os
from pathlib import Path
import re
import sqlite3
import threading
import time
import urllib.request
from renderizar import renderizar

LOG = logging.getLogger('andina-material')
ROOT = Path(os.environ.get('ANDINA_MATERIAL_STATE', str(Path(__file__).parent.parent / '.local' / 'materiales')))
ACTIVE = ('pendiente', 'redactando', 'texto_listo', 'ilustrando')
TEXT_MODEL = os.environ.get('ANDINA_MATERIAL_MODEL', 'llama3.3:70b-1bit')
FIGURE_MODEL = os.environ.get('ANDINA_MATERIAL_FIGURE_MODEL', 'qwen3:14b')
OLLAMA = os.environ.get('ANDINA_MATERIAL_OLLAMA', 'http://127.0.0.1:11434').rstrip('/')


class Store:
    def __init__(self, root):
        self.root = Path(root); self.root.mkdir(parents=True, exist_ok=True, mode=0o700)
        self.path = self.root / 'trabajos.sqlite'
        with self.connect() as db:
            db.execute('PRAGMA journal_mode=WAL')
            db.execute('''CREATE TABLE IF NOT EXISTS materiales (
                id TEXT PRIMARY KEY, owner TEXT NOT NULL, hash TEXT NOT NULL,
                tema TEXT NOT NULL, nivel TEXT NOT NULL, instrucciones TEXT NOT NULL,
                estado TEXT NOT NULL, titulo TEXT NOT NULL DEFAULT '', texto TEXT NOT NULL DEFAULT '',
                mensaje TEXT NOT NULL DEFAULT '', archivos TEXT NOT NULL DEFAULT '[]',
                creado REAL NOT NULL, actualizado REAL NOT NULL,
                intentos_texto INTEGER NOT NULL DEFAULT 0, intentos_figuras INTEGER NOT NULL DEFAULT 0)''')
            columns={row[1] for row in db.execute('PRAGMA table_info(materiales)')}
            if 'texto_revisado' not in columns:db.execute("ALTER TABLE materiales ADD COLUMN texto_revisado TEXT NOT NULL DEFAULT ''")
            db.execute('CREATE INDEX IF NOT EXISTS ix_material_estado_fecha ON materiales(estado,creado)')
            db.execute('CREATE INDEX IF NOT EXISTS ix_material_owner_estado ON materiales(owner,estado)')
            db.execute("UPDATE materiales SET estado='pendiente' WHERE estado='redactando'")
            db.execute("UPDATE materiales SET estado='texto_listo' WHERE estado='ilustrando'")

    @contextmanager
    def connect(self):
        db = sqlite3.connect(self.path, timeout=3); db.row_factory = sqlite3.Row
        try:
            with db: yield db
        finally: db.close()

    def submit(self, data):
        job, owner = data.get('id',''), data.get('propietario','')
        if not re.fullmatch('[a-f0-9]{64}', job) or not re.fullmatch('[a-f0-9]{64}', owner):
            raise ValueError('Identificador inválido')
        values = {}
        for name, maximum in [('tema',200),('nivel',60),('instrucciones',1000)]:
            value = data.get(name,'')
            if not isinstance(value,str) or len(value)>maximum or (name!='instrucciones' and not value.strip()):
                raise ValueError('Revisa tema, nivel e instrucciones')
            values[name] = value.strip()
        digest = hashlib.sha256(json.dumps(values,sort_keys=True,ensure_ascii=False).encode()).hexdigest()
        with self.connect() as db:
            db.execute('BEGIN IMMEDIATE')
            old = db.execute('SELECT hash FROM materiales WHERE id=?',(job,)).fetchone()
            if old:
                if old['hash']!=digest:return 409,{'mensaje':'El identificador ya contiene otra solicitud'}
                return 202,self.get(job,db)
            slots=','.join('?' for _ in ACTIVE)
            if db.execute(f'SELECT count(*) FROM materiales WHERE estado IN ({slots})',ACTIVE).fetchone()[0]>=4:
                return 429,{'mensaje':'Hay cuatro materiales pendientes. Inténtalo al terminar alguno.'}
            if db.execute(f'SELECT count(*) FROM materiales WHERE owner=? AND estado IN ({slots})',(owner,*ACTIVE)).fetchone()[0]:
                return 429,{'mensaje':'Ya tienes un material en proceso'}
            if db.execute('SELECT count(*) FROM materiales').fetchone()[0]>=100:
                return 429,{'mensaje':'Se alcanzó el límite local de 100 materiales. Contacta al administrador para archivar trabajos anteriores.'}
            now=time.time()
            db.execute("INSERT INTO materiales(id,owner,hash,tema,nivel,instrucciones,estado,creado,actualizado) VALUES(?,?,?,?,?,?,'pendiente',?,?)",
                       (job,owner,digest,values['tema'],values['nivel'],values['instrucciones'],now,now))
        return 202,self.get(job)

    def get(self, job, db=None):
        if db is None:
            with self.connect() as con:return self.get(job,con)
        row=db.execute('SELECT * FROM materiales WHERE id=?',(job,)).fetchone()
        if not row:return None
        return {'id':row['id'],'tema':row['tema'],'nivel':row['nivel'],'estado':row['estado'],
                'titulo':row['titulo'],'texto':row['texto'],'textoRevisado':row['texto_revisado'],'mensaje':row['mensaje'],
                'archivos':json.loads(row['archivos']),'modeloTexto':TEXT_MODEL,'modeloFiguras':FIGURE_MODEL,
                'segundos':int(row['actualizado']-row['creado'])}

    def claim(self):
        with self.connect() as db:
            db.execute('BEGIN IMMEDIATE')
            row=db.execute("SELECT * FROM materiales WHERE estado IN ('pendiente','texto_listo') ORDER BY creado LIMIT 1").fetchone()
            if row:
                first=row['estado']=='pendiente'
                stage='redactando' if first else 'ilustrando'
                counter='intentos_texto' if first else 'intentos_figuras'
                db.execute(f'UPDATE materiales SET estado=?,{counter}={counter}+1,actualizado=? WHERE id=?',(stage,time.time(),row['id']))
            return row

    def text_ready(self, job, title, text):
        with self.connect() as db:
            db.execute("UPDATE materiales SET titulo=?,texto=?,archivos='[\"material.txt\"]',estado='texto_listo',actualizado=? WHERE id=?",
                       (title,text,time.time(),job))

    def finish(self, job, files, revised):
        with self.connect() as db:
            db.execute("UPDATE materiales SET estado='listo',archivos=?,texto_revisado=?,actualizado=?,mensaje='' WHERE id=?",
                       (json.dumps(files),revised,time.time(),job))

    def fail(self, job, message):
        with self.connect() as db:
            db.execute("UPDATE materiales SET estado='error',mensaje=?,actualizado=? WHERE id=?",(message,time.time(),job))


def model_json(model, prompt, schema, figure=False):
    options={'num_ctx':2048,'num_predict':512,'num_thread':int(os.environ.get('ANDINA_MATERIAL_THREADS','4')),
             'num_gpu':int(os.environ.get('ANDINA_MATERIAL_FIGURE_GPU_LAYERS','16')) if figure else int(os.environ.get('ANDINA_MATERIAL_GPU_LAYERS','16')),'temperature':0,'seed':42}
    payload={'model':model,'prompt':prompt,'format':schema,'stream':True,'think':False,'keep_alive':0,'options':options}
    req=urllib.request.Request(OLLAMA+'/api/generate',json.dumps(payload).encode(),{'Content-Type':'application/json'})
    deadline=time.monotonic()+600;parts=[];finished=False
    with urllib.request.urlopen(req,timeout=180) as response:
        for raw in response:
            row=json.loads(raw)
            if row.get('error'):raise RuntimeError('El servidor del modelo devolvió un error')
            parts.append(row.get('response',''))
            if sum(map(len,parts))>30000:raise ValueError('Respuesta demasiado larga')
            if row.get('done'):
                LOG.info('Modelo %s: tokens=%s eval_ms=%s carga_ms=%s',model,row.get('eval_count'),int(row.get('eval_duration',0)/1e6),int(row.get('load_duration',0)/1e6))
                if row.get('done_reason')=='length':raise ValueError('El modelo alcanzó el límite de salida; reduce las instrucciones')
                finished=True;break
            if time.monotonic()>deadline:raise TimeoutError('El modelo superó los diez minutos de esta etapa')
    if not finished:raise RuntimeError('El modelo no completó la respuesta')
    return json.loads(''.join(parts))


TEXT_SCHEMA={'type':'object','properties':{'titulo':{'type':'string'},'contenido':{'type':'string'}},'required':['titulo','contenido'],'additionalProperties':False}
FIGURE_SCHEMA={'type':'object','properties':{'textoRevisado':{'type':'string'},'figuras':{'type':'array','maxItems':3,'items':{'type':'object','properties':{
    'tipo':{'type':'string','enum':['triangulo','triangulo_rectangulo','rectangulo','circulo']},'titulo':{'type':'string'},
    'a':{'type':'number'},'b':{'type':'number'},'c':{'type':'number'}},'required':['tipo','titulo','a','b','c'],'additionalProperties':False}}},'required':['textoRevisado','figuras'],'additionalProperties':False}


def worker(store, stop):
    while not stop.is_set():
        row=store.claim()
        if not row:stop.wait(.5);continue
        job=row['id'];directory=store.root/job;directory.mkdir(exist_ok=True,mode=0o700)
        try:
            if row['estado']=='pendiente':
                if row['intentos_texto']>=2:raise RuntimeError('Se agotaron los reintentos de redacción')
                prompt=(f'Eres docente de matemáticas. Redacta en español un material breve de máximo 180 palabras para {row["nivel"]}. '
                        f'Tema: {row["tema"]}. Indicaciones del usuario: {row["instrucciones"]}. '
                        'Incluye concepto, fórmula, un ejemplo y dos ejercicios. Usa texto sencillo y saltos de línea; '
                        'no uses HTML, código, herramientas ni razonamiento interno. Devuelve JSON con titulo y contenido. '
                        'Si describes figuras, incluye medidas coherentes y unidades.')
                result=model_json(TEXT_MODEL,prompt,TEXT_SCHEMA)
                title=result.get('titulo','');text=result.get('contenido','')
                if not isinstance(title,str) or not title.strip() or len(title)>180 or not isinstance(text,str) or not text.strip() or len(text)>12000:
                    raise ValueError('El modelo no devolvió un material válido')
                (directory/'material.txt').write_text(title+'\n\n'+text,encoding='utf-8')
                store.text_ready(job,title,text)
            else:
                if row['intentos_figuras']>=2:raise RuntimeError('Se agotaron los reintentos de ilustración')
                prompt=('Eres docente revisor e ilustrador de matemáticas. El borrador de otro modelo puede tener errores graves. '
                        'Corrige terminología, definiciones, fórmulas y resultados antes de ilustrarlo. '
                        'Resuelve los ejercicios antes de incluirlos y escribe sus respuestas. Sustituye cualquier ejercicio imposible o ambiguo por uno válido. '
                        'En triángulos rectángulos la hipotenusa siempre supera a cada cateto. '
                        'Devuelve textoRevisado con máximo 140 palabras en español y figuras con entre una y tres ilustraciones. '
                        'Solo puedes proponer triangulo (a,b,c son sus lados), triangulo_rectangulo (a,b son catetos; c=0), '
                        'rectangulo (a=base,b=altura,c=0), circulo (a=radio,b=0,c=0). '
                        'Las medidas deben coincidir con el texto. No inventes una figura incompatible. '
                        'No generes SVG, código, rutas ni instrucciones de sistema. Si no hay figura geométrica pertinente, devuelve figuras vacías. '
                        'Material completo:\n'+row['titulo']+'\n'+row['texto'])
                result=model_json(FIGURE_MODEL,prompt,FIGURE_SCHEMA,True)
                revised=result.get('textoRevisado')
                if not isinstance(revised,str) or not revised.strip() or len(revised)>12000:raise ValueError('No se recibió una revisión de texto válida')
                figures=result.get('figuras')
                if not isinstance(figures,list) or len(figures)>3:raise ValueError('Respuesta de figuras inválida')
                files=renderizar(directory,row['titulo'],revised,figures,TEXT_MODEL,FIGURE_MODEL)
                store.finish(job,files,revised)
            LOG.info('Etapa de material terminada: %s %s',job[:8],row['estado'])
        except Exception as exc:
            LOG.exception('Falló material %s',job[:8])
            # El borrador ya creado sigue disponible aunque falle la segunda etapa.
            message=str(exc)[:220] if isinstance(exc,ValueError) else 'Revisa el servicio local y sus registros. El borrador guardado se conserva.'
            store.fail(job,'No se completó esta etapa. '+message)


class Api(ThreadingHTTPServer):
    daemon_threads=True
    request_queue_size=8
    def __init__(self,address,store,key):
        super().__init__(address,Handler);self.store=store;self.key=key;self.slots=threading.BoundedSemaphore(8)
    def process_request(self,request,address):
        if not self.slots.acquire(blocking=False):
            try:request.sendall(b'HTTP/1.1 503 Service Unavailable\r\nContent-Length: 0\r\nConnection: close\r\n\r\n')
            finally:self.shutdown_request(request)
            return
        try:super().process_request(request,address)
        except Exception:self.slots.release();raise
    def process_request_thread(self,request,address):
        try:request.settimeout(8);super().process_request_thread(request,address)
        finally:self.slots.release()


class Handler(BaseHTTPRequestHandler):
    def log_message(self,*args):pass
    def send_json(self,status,data):
        raw=json.dumps(data,ensure_ascii=False).encode();self.send_response(status)
        self.send_header('Content-Type','application/json; charset=utf-8');self.send_header('Content-Length',str(len(raw)))
        self.send_header('Cache-Control','no-store');self.send_header('Connection','close');self.end_headers();self.wfile.write(raw)
    def authorized(self):
        if not hmac.compare_digest(self.headers.get('X-Andina-Key',''),self.server.key):
            self.send_json(401,{'mensaje':'No autorizado'});return False
        return True
    def do_GET(self):
        if not self.authorized():return
        if self.path=='/health':return self.send_json(200,{'estado':'disponible','modeloTexto':TEXT_MODEL,'modeloFiguras':FIGURE_MODEL})
        match=re.fullmatch(r'/v1/materiales/([a-f0-9]{64})(?:/archivos/([a-z0-9.-]{1,60}))?',self.path)
        if not match:return self.send_json(404,{'mensaje':'No encontrado'})
        result=self.server.store.get(match[1])
        if not result:return self.send_json(404,{'mensaje':'No encontrado'})
        if not match[2]:return self.send_json(200,result)
        filename=match[2]
        if filename not in result['archivos']:return self.send_json(404,{'mensaje':'Archivo no disponible'})
        file=self.server.store.root/match[1]/filename
        if not file.is_file() or file.stat().st_size>8_000_000:return self.send_json(404,{'mensaje':'Archivo no disponible'})
        data=file.read_bytes();mime={'.pdf':'application/pdf','.png':'image/png','.svg':'image/svg+xml','.txt':'text/plain; charset=utf-8','.docx':'application/vnd.openxmlformats-officedocument.wordprocessingml.document'}.get(file.suffix,'application/octet-stream')
        self.send_response(200);self.send_header('Content-Type',mime);self.send_header('Content-Length',str(len(data)))
        self.send_header('Cache-Control','no-store');self.send_header('X-Content-Type-Options','nosniff');self.end_headers();self.wfile.write(data)
    def do_POST(self):
        if not self.authorized():return
        if self.path!='/v1/materiales':return self.send_json(404,{'mensaje':'No encontrado'})
        try:
            size=int(self.headers.get('Content-Length','0'))
            if size<1 or size>20000 or self.headers.get('Transfer-Encoding'):return self.send_json(413,{'mensaje':'Solicitud demasiado grande'})
            data=json.loads(self.rfile.read(size))
            if not isinstance(data,dict):raise ValueError('JSON inválido')
            code,result=self.server.store.submit(data);self.send_json(code,result)
        except (ValueError,TypeError):self.send_json(400,{'mensaje':'Revisa tema, nivel e instrucciones'})
        except sqlite3.OperationalError:self.send_json(503,{'mensaje':'Servicio ocupado'})


def main():
    os.umask(0o077)
    key=os.environ.get('ANDINA_MATERIAL_IA_API_KEY','')
    if len(key)<32:raise SystemExit('Configura ANDINA_MATERIAL_IA_API_KEY')
    logging.basicConfig(level=logging.INFO,format='%(asctime)s %(levelname)s %(message)s')
    store=Store(ROOT);stop=threading.Event()
    threading.Thread(target=worker,args=(store,stop),daemon=True).start()
    server=Api(('127.0.0.1',int(os.environ.get('ANDINA_MATERIAL_PORT','8096'))),store,key)
    LOG.info('Materiales IA listos: 127.0.0.1:%s',server.server_port)
    try:server.serve_forever()
    except KeyboardInterrupt:pass
    finally:stop.set();server.server_close()


if __name__=='__main__':main()
