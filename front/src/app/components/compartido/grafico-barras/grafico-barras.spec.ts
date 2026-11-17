import { registerLocaleData } from '@angular/common';
import localeEsPE from '@angular/common/locales/es-PE';
import { LOCALE_ID } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Barra, GraficoBarras } from './grafico-barras';

registerLocaleData(localeEsPE);

function renderizar(datos: Barra[], entradas: Record<string, unknown> = {}) {
  TestBed.configureTestingModule({
    imports: [GraficoBarras],
    providers: [{ provide: LOCALE_ID, useValue: 'es-PE' }],
  });
  const fixture = TestBed.createComponent(GraficoBarras);
  fixture.componentRef.setInput('titulo', 'Prueba');
  fixture.componentRef.setInput('datos', datos);
  for (const [k, v] of Object.entries(entradas)) {
    fixture.componentRef.setInput(k, v);
  }
  fixture.detectChanges();
  const el = fixture.nativeElement as HTMLElement;
  return {
    el,
    fracciones: [...el.querySelectorAll<HTMLElement>('.barra')].map((b) =>
      b.style.getPropertyValue('--fraccion'),
    ),
    valores: [...el.querySelectorAll('.valor')].map((v) =>
      v.textContent?.trim(),
    ),
  };
}

describe('GraficoBarras', () => {
  it('escala respecto al mayor valor y escribe cada valor', () => {
    const { fracciones, valores } = renderizar([
      { etiqueta: 'A', valor: 10 },
      { etiqueta: 'B', valor: 5 },
    ]);
    expect(fracciones).toEqual(['1', '0.5']);
    expect(valores).toEqual(['10', '5']);
  });

  it('usa el máximo fijo y el sufijo (porcentajes)', () => {
    const { fracciones, valores } = renderizar([{ etiqueta: 'A', valor: 25 }], {
      maximo: 100,
      sufijo: ' %',
    });
    expect(fracciones).toEqual(['0.25']);
    expect(valores).toEqual(['25 %']);
  });

  it('muestra "—" y barra vacía cuando el valor es nulo', () => {
    const { fracciones, valores } = renderizar([
      { etiqueta: 'A', valor: 4 },
      { etiqueta: 'B', valor: null },
    ]);
    expect(fracciones).toEqual(['1', '0']);
    expect(valores).toEqual(['4', '—']);
  });

  it('recorta a 12 barras y avisa que la tabla tiene todas', () => {
    const datos = Array.from({ length: 15 }, (_, i) => ({
      etiqueta: `C${i}`,
      valor: i,
    }));
    const { el, fracciones } = renderizar(datos);
    expect(fracciones.length).toBe(12);
    expect(el.querySelector('.aviso')?.textContent).toContain('12 de 15');
  });

  it('oculta las barras a lectores de pantalla (la tabla es el equivalente accesible)', () => {
    const { el } = renderizar([{ etiqueta: 'A', valor: 1 }]);
    expect(el.querySelector('.barras')?.getAttribute('aria-hidden')).toBe(
      'true',
    );
    expect(el.querySelector('figcaption')?.textContent).toContain('Prueba');
  });
});
