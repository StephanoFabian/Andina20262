"""Herramientas permitidas: geometría declarativa -> SVG, PNG, PDF y Word.

Solo recibe números y texto. Recalcula las medidas y no ejecuta código del modelo.
"""
import base64
import html
import json
import math
from pathlib import Path
import shutil
import subprocess
import tempfile
from PIL import Image,ImageDraw,ImageFont
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet
from reportlab.lib.units import cm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import SimpleDocTemplate,Paragraph,Spacer,Image as PdfImage


def geometry(spec):
    kind=spec.get('tipo');title=spec.get('titulo','Figura geométrica')
    if kind not in ['triangulo','triangulo_rectangulo','rectangulo','circulo'] or not isinstance(title,str) or len(title)>150:
        raise ValueError('Figura no soportada')
    def number(key):
        n=spec.get(key)
        if isinstance(n,bool) or not isinstance(n,(float,int)) or not math.isfinite(n) or not 0<n<=1000:
            raise ValueError('Las medidas deben ser positivas, finitas y de hasta 1000 unidades')
        return float(n)
    a=number('a');data={'tipo':kind,'titulo':title,'a':a}
    if kind=='circulo':
        data.update(area=math.pi*a*a,perimetro=2*math.pi*a)
    else:
        b=number('b');data['b']=b
        if kind=='rectangulo':data.update(area=a*b,perimetro=2*(a+b))
        else:
            c=math.hypot(a,b) if kind=='triangulo_rectangulo' else number('c')
            if a+b<=c or a+c<=b or b+c<=a:raise ValueError('Los lados propuestos no forman un triángulo')
            half=(a+b+c)/2
            data.update(c=c,area=math.sqrt(half*(half-a)*(half-b)*(half-c)),perimetro=a+b+c)
    return data


def figure(directory,index,data):
    image=Image.new('RGB',(1100,700),'white');draw=ImageDraw.Draw(image)
    font_path='/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf'
    font=ImageFont.truetype(font_path,25) if Path(font_path).exists() else ImageFont.load_default()
    small=ImageFont.truetype(font_path,21) if Path(font_path).exists() else font
    svg=['<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1100 700"><rect width="1100" height="700" fill="white"/>']
    def text(x,y,value):
        draw.text((x,y),value,font=font,fill='#153c30')
        svg.append(f'<text x="{x}" y="{y+25}" font-family="DejaVu Sans,sans-serif" font-size="25" fill="#153c30">{html.escape(value)}</text>')
    def lines(points,close=True):
        path=points+[points[0]] if close else points
        draw.line(path,fill='#216b51',width=5)
        tag='polygon' if close else 'polyline'
        svg.append(f'<{tag} points="'+ ' '.join(f'{x:.3f},{y:.3f}' for x,y in points)+'" fill="none" stroke="#216b51" stroke-width="5"/>')
    text(65,35,data['titulo'])
    a=data['a'];kind=data['tipo']
    if kind=='circulo':
        radius=210;cx,cy=540,350
        draw.ellipse((cx-radius,cy-radius,cx+radius,cy+radius),outline='#216b51',width=5)
        svg.append(f'<circle cx="{cx}" cy="{cy}" r="{radius}" fill="none" stroke="#216b51" stroke-width="5"/>')
        lines([(cx,cy),(cx+radius,cy)],False);text(cx+20,cy-40,f'r = {a:g}')
    elif kind=='rectangulo':
        b=data['b'];scale=min(700/a,350/b);w,h=a*scale,b*scale;x,y=(1100-w)/2,180
        lines([(x,y),(x+w,y),(x+w,y+h),(x,y+h)]);text(x+w/2-30,y+h+15,f'{a:g}');text(x+w+15,y+h/2,f'{b:g}')
    else:
        b,c=data['b'],data['c']
        # Base de longitud b; el tercer vértice se obtiene por ley de cosenos.
        x=(a*a+b*b-c*c)/(2*b);height=math.sqrt(max(0,a*a-x*x))
        if height/a<0.03:raise ValueError('El triángulo es demasiado plano para ilustrarlo con claridad')
        width=max(b,x)-min(0,x);scale=min(700/width,340/height)
        offset=(1100-width*scale)/2-min(0,x)*scale;baseline=530
        points=[(offset,baseline),(offset+b*scale,baseline),(offset+x*scale,baseline-height*scale)]
        lines(points)
        text(offset+b*scale/2-30,baseline+12,f'b = {b:g}')
        text((points[0][0]+points[2][0])/2-90,(points[0][1]+points[2][1])/2,f'a = {a:g}')
        text((points[1][0]+points[2][0])/2+15,(points[1][1]+points[2][1])/2,f'c = {c:.4g}')
        if kind=='triangulo_rectangulo':lines([(offset+26,baseline),(offset+26,baseline-26),(offset,baseline-26)],False)
    text(65,610,f'Área = {data["area"]:.4g} u²     Perímetro = {data["perimetro"]:.4g} u')
    png=f'figura-{index}.png';svg_name=f'figura-{index}.svg'
    image.save(directory/png);svg.append('</svg>');(directory/svg_name).write_text(''.join(svg),encoding='utf-8')
    return png,svg_name


def renderizar(directory,title,text,specs,text_model,figure_model):
    directory=Path(directory)
    if not isinstance(title,str) or not isinstance(text,str) or len(text)>12000 or not isinstance(specs,list) or len(specs)>3:
        raise ValueError('Contenido fuera de los límites del generador')
    figures=[geometry(spec) for spec in specs]
    (directory/'material-revisado.txt').write_text(title+'\n\n'+text,encoding='utf-8')
    files=['material.txt','material-revisado.txt'];images=[]
    for index,data in enumerate(figures,1):
        png,svg=figure(directory,index,data);files.extend([png,svg]);images.append(png)
    (directory/'geometria-verificada.json').write_text(json.dumps(figures,ensure_ascii=False,indent=2))
    font=Path('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf')
    if font.exists():
        pdfmetrics.registerFont(TTFont('AndinaSans',str(font)));family='AndinaSans'
    else:family='Helvetica'
    styles=getSampleStyleSheet()
    for name in ['Title','Heading2','BodyText']:styles[name].fontName=family
    styles['BodyText'].leading=16;styles['BodyText'].spaceAfter=9
    story=[Paragraph(html.escape(title),styles['Title']),Spacer(1,.35*cm)]
    for paragraph in text.splitlines():
        if paragraph.strip():story.append(Paragraph(html.escape(paragraph),styles['BodyText']))
    for data,png in zip(figures,images):
        story.extend([Spacer(1,.5*cm),Paragraph(html.escape(data['titulo']),styles['Heading2']),PdfImage(str(directory/png),width=17*cm,height=10.82*cm)])
    story.extend([Spacer(1,.5*cm),Paragraph('Borrador generado con IA. Las medidas de las figuras se recalcularon por código. Revisa los contenidos antes de usarlos en clase.',styles['BodyText'])])
    SimpleDocTemplate(str(directory/'material.pdf'),rightMargin=1.6*cm,leftMargin=1.6*cm,topMargin=1.6*cm,bottomMargin=1.6*cm).build(story)
    files.append('material.pdf')
    # Texto e imágenes se entregan a Writer como HTML controlado, sin macros ni scripts.
    body=''.join('<p>'+html.escape(p)+'</p>' for p in text.splitlines() if p.strip())
    for png in images:
        encoded=base64.b64encode((directory/png).read_bytes()).decode('ascii')
        body+='<p><img width="600" height="382" src="data:image/png;base64,'+encoded+'"/></p>'
    body+='<p>Borrador generado con IA. Revisa los contenidos antes de usarlos en clase.</p>'
    source=directory/'material.html';source.write_text('<!doctype html><html><head><meta charset="utf-8"/></head><body><h1>'+html.escape(title)+'</h1>'+body+'</body></html>',encoding='utf-8')
    lo=shutil.which('libreoffice')
    if lo:
        with tempfile.TemporaryDirectory(prefix='andina-writer-') as profile:
            subprocess.run([lo,'-env:UserInstallation='+Path(profile).as_uri(),'--headless','--convert-to','docx:Office Open XML Text','--outdir',str(directory),str(source)],capture_output=True,timeout=45,check=True)
        if (directory/'material.docx').is_file():files.append('material.docx')
    (directory/'manifiesto.json').write_text(json.dumps({'modeloTexto':text_model,'modeloFiguras':figure_model,'archivos':files,'figuras':figures},ensure_ascii=False,indent=2))
    return files
