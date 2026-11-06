#!/usr/bin/env python3
"""Medición de lectura en una API de prueba con 10.000 grados Carga 000001–010000.
No inserta ni elimina datos. Credenciales: ANDINA_TEST_PERSONA y ANDINA_TEST_PASSWORD.
"""
import json,os,time,statistics,concurrent.futures,urllib.request,urllib.parse
from pathlib import Path
BASE=os.environ.get('API_BASE','http://127.0.0.1:18081')
def request(path,token='',data=None):
 req=urllib.request.Request(BASE+path,data=json.dumps(data).encode() if data else None,headers={'Content-Type':'application/json',**({'Authorization':'Bearer '+token} if token else {})})
 with urllib.request.urlopen(req,timeout=20) as response:return json.loads(response.read())
token=request('/login',data={'idPersona':int(os.environ.get('ANDINA_TEST_PERSONA','1')),'passwordPersona':os.environ['ANDINA_TEST_PASSWORD']})['token']
def read(i):
 params={'size':50,'page':i%200,'q':'Carga'} if i%2 else {'size':50,'page':i%200}
 started=time.perf_counter();result=request('/api/grados/pagina?'+urllib.parse.urlencode(params),token)
 assert len(result['content'])<=50 and result['totalElements']>=10000
 assert 'password' not in json.dumps(result).lower()
 return (time.perf_counter()-started)*1000
for i in range(10):read(i)
started=time.perf_counter()
with concurrent.futures.ThreadPoolExecutor(max_workers=20) as pool:durations=list(pool.map(read,range(200)))
elapsed=time.perf_counter()-started;ordered=sorted(durations)
report={'records':request('/api/grados/pagina?size=1&q=Carga',token)['totalElements'],'requests':len(durations),'concurrency':20,'pageSize':50,'elapsedSeconds':round(elapsed,3),'p50Ms':round(statistics.median(durations),2),'p95Ms':round(ordered[int(len(ordered)*.95)-1],2),'maxMs':round(max(durations),2),'errors':0,'scope':'Localhost; PostgreSQL 18.6; una instancia; lectura con paginación y prefijo. No es una garantía de capacidad de producción.'}
Path('/tmp/andina-verificacion/carga.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n');print(json.dumps(report,ensure_ascii=False,indent=2))
