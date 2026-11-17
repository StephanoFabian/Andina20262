#!/usr/bin/env python3
"""CRUD y compatibilidad de Edisson. Requiere API y BD aisladas de prueba."""
import json,os,urllib.request,urllib.error,time
from pathlib import Path
if os.environ.get('ANDINA_TEST_WRITES')!='1':raise SystemExit('Requiere ANDINA_TEST_WRITES=1 y una base de pruebas aislada')
BASE=os.environ.get('API_BASE','http://127.0.0.1:18081');token='';checks=[]
def call(method,path,data=None,status=200):
 request=urllib.request.Request(BASE+path,data=json.dumps(data).encode() if data is not None else None,headers={'Content-Type':'application/json',**({'Authorization':'Bearer '+token} if token else {})},method=method)
 try:
  with urllib.request.urlopen(request,timeout=15) as response:code=response.status;text=response.read().decode()
 except urllib.error.HTTPError as error:code=error.code;text=error.read().decode()
 assert code==status,(path,code,text[:500])
 try:return json.loads(text) if text else None
 except json.JSONDecodeError:return text
who=int(os.environ.get('ANDINA_TEST_PERSONA','1'))
for i in range(20):
 try:token=call('POST','/login',{'idPersona':who,'passwordPersona':os.environ['ANDINA_TEST_PASSWORD']})['token'];break
 except OSError:time.sleep(1)
else:raise RuntimeError('La API no está disponible')
convenio=dict(nombre='QA Edisson convenio',entidad='Gobierno de prueba',tipoEntidad='GOBIERNO_REGIONAL',region='Cusco',fechaInicio='2026-01-01',fechaFin='2026-11-15',presupuesto=12345.67,estado='EN_RENOVACION')
c=call('POST','/api/convenios',convenio,201);cid=c['idConvenio'];assert c['idColegio'] is None and c['presupuesto']==12345.67
page=call('GET','/api/convenios/pagina?q=QA%20Edisson');assert any(x['idConvenio']==cid for x in page['content'])
convenio.update(region='Lima',presupuesto=23456.78);c=call('PUT','/api/convenios/'+str(cid),convenio);assert c['region']=='Lima' and c['presupuesto']==23456.78
c=call('GET','/api/convenios/'+str(cid));assert c['entidad']==convenio['entidad'];checks.append('Convenio de Edisson sin colegio: crear, paginar, editar y leer sin perder campos')
p=call('GET','/api/convenios/por-vencer/pagina?dias=90');assert any(x['idConvenio']==cid for x in p['content']);checks.append('Reporte paginado de vencimientos con días y presupuesto')
call('POST','/api/convenios',dict(convenio,presupuesto=-1),400);call('GET','/api/convenios/por-vencer/pagina?dias=3651',status=400)
checks.append('Validaciones de presupuesto y plazo')
colegio=call('POST','/api/colegios',dict(nombre='QA Colegio legado',departamento='Lima',provincia='Lima',distrito='Lima',comunidad='QA',tipo_zona='RURAL'),201)
legacy=call('POST','/api/convenios',dict(nombre='QA Convenio legado',institucion='Institución anterior',idColegio=colegio['idColegio'],fechaInicio='2026-01-01',fechaFin='2026-12-31',estado='VIGENTE'),201)
updated=call('PUT','/api/convenios/'+str(legacy['idConvenio']),convenio);assert updated['idColegio']==colegio['idColegio'];checks.append('Contrato anterior compatible y colegio conservado al editar con el nuevo formulario')
course=call('POST','/curso',dict(nombre_curso='QA Edisson curso',descripcion='Prueba',area='Ciencias'),201)
m=call('POST','/materiales/nuevo',dict(titulo='QA Edisson material',tipo='GUIA_QA',idPersona=who,urlArchivo='https://example.test/qa',fechaPublicacion='2026-11-06'),201);mid=m['idMaterial']
call('POST','/materiales-cursos/nuevo',dict(idMaterial=mid,idCurso=course['id_curso']),201)
p=call('GET','/materiales-cursos/pagina?idMaterial='+str(mid));assert p['totalElements']==1 and p['content'][0]['curso']=='QA Edisson curso';checks.append('Asociación material-curso y nombres obtenidos en la misma página')
p=call('GET','/materiales/consulta/pagina?q=QA%20Edisson');assert any(x['idMaterial']==mid for x in p['content'])
p=call('GET','/materiales/consulta/pagina?idCurso='+str(course['id_curso']));assert p['totalElements']==1 and p['content'][0]['idMaterial']==mid;checks.append('Búsquedas paginadas por título y por curso')
assert 'GUIA_QA' in call('GET','/materiales/tipos?q=GUIA');checks.append('Sugerencias de tipos limitadas a 20 resultados')
p=call('GET','/materiales/reporte-por-tipo/pagina');assert any(x['tipo']=='GUIA_QA' and x['cantidad']>=1 for x in p['content'])
p=call('GET','/materiales-cursos/reporte-materiales-por-curso/pagina');assert any(x['curso']=='QA Edisson curso' and x['cantidadMateriales']==1 for x in p['content']);checks.append('Reportes de material por tipo y cantidad por curso')
for path in ['/materiales/consulta/pagina','/materiales/reporte-por-tipo/pagina','/materiales-cursos/reporte-materiales-por-curso/pagina','/api/convenios/por-vencer/pagina']:
 call('GET',path+'?size=101',status=400)
checks.append('Límite de página aplicado a búsquedas y reportes')
call('DELETE','/materiales/'+str(mid),status=409)
call('DELETE',f"/materiales-cursos/{mid}/{course['id_curso']}")
call('DELETE','/materiales/'+str(mid));call('DELETE','/curso/'+str(course['id_curso']),status=204)
call('DELETE','/api/convenios/'+str(cid),status=204);call('DELETE','/api/convenios/'+str(legacy['idConvenio']),status=204);call('DELETE','/api/colegios/'+str(colegio['idColegio']),status=204)
checks.append('Eliminación y protección de referencias sin cascada')
report={'checks':checks,'passed':len(checks)};Path('/tmp/andina-verificacion/edisson-api.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n');print(json.dumps(report,ensure_ascii=False,indent=2))
