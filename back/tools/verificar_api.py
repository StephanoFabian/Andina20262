#!/usr/bin/env python3
"""Prueba destructiva solo sobre una BD de verificación; nunca usar contra producción.
API_BASE=http://127.0.0.1:18081 ANDINA_TEST_PASSWORD=... python3 tools/verificar_api.py
El administrador de prueba debe existir (ANDINA_TEST_PERSONA, por defecto 1).
"""
import json, os, urllib.request, urllib.error, time, statistics, concurrent.futures
from pathlib import Path
BASE=os.environ.get('API_BASE','http://127.0.0.1:18081').rstrip('/')
if os.environ.get('ANDINA_TEST_WRITES')!='1':raise SystemExit('Define ANDINA_TEST_WRITES=1 únicamente para una API conectada a una base de pruebas aislada.')
TOKEN=''; checks=[]
def call(method,path,data=None,expect=200,token=None):
 headers={'Content-Type':'application/json'}
 if token is None:token=TOKEN
 if token:headers['Authorization']='Bearer '+token
 req=urllib.request.Request(BASE+path,data=json.dumps(data).encode() if data is not None else None,headers=headers,method=method)
 try:
  with urllib.request.urlopen(req,timeout=20) as r:status=r.status;body=r.read().decode();location=r.headers.get('Location','')
 except urllib.error.HTTPError as e:status=e.code;body=e.read().decode();location=''
 assert status==expect,f'{method} {path}: esperado {expect}, obtenido {status}: {body[:350]}'
 try:result=json.loads(body) if body else None
 except json.JSONDecodeError:result=body
 return result,location
TOKEN=call('POST','/login',{'idPersona':int(os.environ.get('ANDINA_TEST_PERSONA','1')),'passwordPersona':os.environ['ANDINA_TEST_PASSWORD']},token='')[0]['token']
call('GET','/api/grados/pagina',expect=401,token='');checks.append('API exige autenticación')
mods=json.loads((Path(__file__).resolve().parents[1]/'tools/modulos_prueba.json').read_text())
configs={m['key']:m for m in mods};ids={};created={};stamp=str(time.time_ns())[-8:]
values={
 'colegios':dict(nombre='QA'+stamp,departamento='Lima',provincia='Lima',distrito='Lima',comunidad='Prueba',tipo_zona='RURAL'),
 'aulas':dict(nombre='Aula QA',seccion='A',capacidad=35),
 'grados':dict(nombre='Grado QA '+stamp,nivel='Secundaria'),
 'cursos':dict(nombre_curso='Curso QA',descripcion='Curso para verificar el frontend',area='Matematica'),
 'periodos-academicos':dict(nombre='Periodo QA',fechaInicio='2026-01-01',fechaFin='2026-12-31',estado='ACTIVO'),
 'roles':dict(detalle='DOCENTE'),
 'personas':dict(nombresPersona='Docente QA',apellidosPersona='Verificacion',fechaNacimientoPersona='1990-01-01',emailPersona='qa@example.test',estadoPersona='ACTIVO',passwordPersona='PruebaDocente2026!'),
 'matriculas':{},
 'detalles-matricula':dict(fechaMatricula='2026-04-01',estado='VIGENTE'),
 'perfiles-academicos':dict(detallePA='Perfil QA',notasPA=18),
 'materiales':dict(titulo='Material QA',descripcion='Prueba',tipo='DOCUMENTO',urlArchivo='https://example.test/material.pdf',fechaPublicacion='2026-04-01'),
 'materiales-cursos':{},
 'asignaciones':dict(id_aula='Aula QA',modalidad='VIRTUAL',horassemanales=4),
 'sesiones-virtuales':dict(titulo='Sesion QA',fecha='2026-11-09',horaInicio='08:00',horaFin='09:00',enlace='https://example.test/clase',estado='PROGRAMADA'),
 'asistencias':dict(estado='PRESENTE',observacion='Prueba'),
 'horarios':dict(diaSemana=1,horaInicio='08:00',horaFin='09:00'),
 'convenios':dict(nombre='Convenio QA',institucion='Institucion QA',fechaInicio='2026-01-01',fechaFin='2026-12-31',estado='VIGENTE'),
 'incidencias':dict(titulo='Incidencia QA',descripcion='Descripcion de prueba',fecha='2026-11-06',estado='ABIERTA',prioridad='MEDIA')}
order=['colegios','aulas','grados','cursos','periodos-academicos','roles','personas','matriculas','detalles-matricula','perfiles-academicos','materiales','materiales-cursos','asignaciones','sesiones-virtuales','asistencias','horarios','convenios','incidencias']
for key in order:
 m=configs[key];dto=dict(values[key])
 for field in m['fields']:
  if field['type']=='reference':dto[field['key']]=ids[field['resource']]
 row,loc=call('POST',m['path']+m['createPath'],dto,201)
 identifier=row.get(m['id']) or (int(loc.rsplit('/',1)[1]) if loc else None)
 assert identifier is not None,f'{key} sin ID'
 ids[key]=identifier;dto[m['id']]=identifier;created[key]=dto
 if m['editable']:
  found=call('GET',m['path']+'/'+str(identifier))[0]
  assert found[m['id']]==identifier,(key,found)
  for field in m['fields']:
   if field['type']=='reference':assert found[field['key']]==dto[field['key']],(key,field,found)
  call('PUT',m['path']+m['updatePath'].replace('{id}',str(identifier)),dto)
 page=call('GET',m['path']+'/pagina?page=0&size=10')[0]
 assert len(page['content'])<=10 and page['totalElements']>0
 if page['totalElements']<=10:assert any(r[m['id']]==identifier for r in page['content']),key
 assert 'password' not in json.dumps(page).lower()
 checks.append(key+': crear, leer, '+('editar, ' if m['editable'] else '')+'paginar')
 print('OK',checks[-1],flush=True)
# DB and domain validation, including concurrent schedule overlap.
call('POST','/api/asistencias',created['asistencias'],409)
wrong=dict(created['sesiones-virtuales'],horaFin='07:00');call('POST','/api/sesiones-virtuales',wrong,400)
wrong=dict(created['horarios'],horaInicio='08:30',horaFin='09:30');call('POST','/api/horarios',wrong,400)
wrong=dict(created['convenios'],fechaFin='2025-01-01');call('POST','/api/convenios',wrong,400)
wrong=dict(created['incidencias'],idPersona=999999999);call('POST','/api/incidencias',wrong,404)
for query in ['size=101','size=0','page=-1','page=10001']:call('GET','/api/grados/pagina?'+query,expect=400)
checks.append('Límites, referencias, duplicados y rangos inválidos rechazados')
docente=call('POST','/login',{'idPersona':ids['personas'],'passwordPersona':values['personas']['passwordPersona']},token='')[0]['token']
call('POST','/api/grados',dict(nombre='No permitido',nivel='Primaria'),403,docente)
call('POST','/api/convenios',created['convenios'],403,docente)
checks.append('Permisos docentes: grados y convenios bloqueados')
# Same assignment is locked while checking overlaps, so at most one concurrent write wins.
import urllib.error
conflict=dict(created['horarios'],diaSemana=2,horaInicio='10:00',horaFin='11:00')
def write_schedule(_):
 headers={'Content-Type':'application/json','Authorization':'Bearer '+TOKEN}
 request=urllib.request.Request(BASE+'/api/horarios',data=json.dumps(conflict).encode(),headers=headers,method='POST')
 try:
  with urllib.request.urlopen(request,timeout=20) as r:return r.status
 except urllib.error.HTTPError as e:return e.code
with concurrent.futures.ThreadPoolExecutor(max_workers=8) as pool:codes=list(pool.map(write_schedule,range(8)))
assert codes.count(201)==1 and sum(c in (400,409) for c in codes)==7,codes
checks.append('8 horarios concurrentes superpuestos: 1 creado y 7 rechazados por validación o unicidad')
# Deletion in reverse dependency order for every supported module.
for key in reversed(order):
 if key=='horarios':
  page=call('GET','/api/horarios/pagina?size=100')[0]
  for row in page['content']:
   if row['idHorario']!=ids[key]:call('DELETE','/api/horarios/'+str(row['idHorario']),expect=204)
 m=configs[key];row=created[key];path=m['deletePath'].replace('{id}',str(row[m['id']]))
 for field,value in row.items():path=path.replace('{'+field+'}',str(value))
 expected=200 if key in ['roles','personas','perfiles-academicos','materiales','materiales-cursos','detalles-matricula'] else 204
 call('DELETE',m['path']+path,expect=expected)
 checks.append(key+': eliminar')
report={'checks':checks,'passed':len(checks),'base':BASE}
out=Path(os.environ.get('ANDINA_TEST_REPORT','/tmp/andina-verificacion/api.json'));out.parent.mkdir(parents=True,exist_ok=True);out.write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
print(json.dumps({'passed':len(checks),'report':str(out)},ensure_ascii=False))
