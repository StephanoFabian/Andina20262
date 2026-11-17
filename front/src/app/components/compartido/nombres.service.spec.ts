import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { NombresService } from './nombres.service';
import { fechaPeruana, horaPeruana, textoLegible, tonoDe } from './formato';
import { environment } from '../../../environments/environment';
const api = environment.base;

/** Deja correr el microtask que agrupa las peticiones */
const ciclo = () => Promise.resolve();

describe('NombresService', () => {
  let nombres: NombresService;
  let requests: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    });
    nombres = TestBed.inject(NombresService);
    requests = TestBed.inject(HttpTestingController);
  });
  afterEach(() => requests.verify());

  it('pide una sola página para todas las filas y no una petición por fila', async () => {
    for (const id of [1, 2, 3, 1, 2])
      expect(nombres.nombre('colegio', id).estado).toBe('cargando');
    await ciclo();
    const peticion = requests.expectOne(
      (r) => r.url === `${api}/api/colegios/pagina`,
    );
    expect(peticion.request.params.get('size')).toBe('100');
    peticion.flush({
      content: [
        { idColegio: 1, nombre: 'Colegio Uno' },
        { idColegio: 2, nombre: 'Colegio Dos' },
        { idColegio: 3, nombre: 'Colegio Tres' },
      ],
      totalElements: 3,
      number: 0,
      size: 100,
    });
    expect(nombres.nombre('colegio', 2)).toEqual({
      texto: 'Colegio Dos',
      estado: 'ok',
    });
    await ciclo();
  });

  it('pide aparte solo los que no vinieron en la página, una vez cada uno', async () => {
    nombres.nombre('persona', 1);
    nombres.nombre('persona', 250);
    await ciclo();
    requests
      .expectOne((r) => r.url === `${api}/personas/pagina`)
      .flush({
        content: [
          { idPersona: 1, nombresPersona: 'Ana', apellidosPersona: 'Quispe' },
        ],
        totalElements: 300,
        number: 0,
        size: 100,
      });
    expect(nombres.nombre('persona', 1).texto).toBe('Ana Quispe');
    nombres.nombre('persona', 250);
    nombres.nombre('persona', 250);
    await ciclo();
    requests
      .expectOne(`${api}/personas/250`)
      .flush({ idPersona: 250, nombresPersona: 'Luis', apellidosPersona: '' });
    expect(nombres.nombre('persona', 250).texto).toBe('Luis');
  });

  it('muestra el número cuando el nombre no se puede obtener', async () => {
    nombres.nombre('grado', 9);
    await ciclo();
    requests
      .expectOne((r) => r.url === `${api}/api/grados/pagina`)
      .flush('error', { status: 500, statusText: 'Error' });
    requests
      .expectOne(`${api}/api/grados/9`)
      .flush('no existe', { status: 404, statusText: 'Not Found' });
    expect(nombres.nombre('grado', 9)).toEqual({
      texto: '#9',
      estado: 'falta',
    });
    expect(nombres.nombre('grado', null).texto).toBe('—');
  });

  it('describe una asignación con su curso y su docente', async () => {
    nombres.nombre('asignacion', 7);
    await ciclo();
    requests
      .expectOne((r) => r.url === `${api}/asignacion/pagina`)
      .flush({
        content: [{ id_asignacion: 7, id_curso: 3, id_persona: 5 }],
        totalElements: 1,
        number: 0,
        size: 100,
      });
    expect(nombres.nombre('asignacion', 7).estado).toBe('cargando');
    await ciclo();
    requests
      .expectOne((r) => r.url === `${api}/personas/pagina`)
      .flush({
        content: [
          { idPersona: 5, nombresPersona: 'Rosa', apellidosPersona: 'Mamani' },
        ],
        totalElements: 1,
        number: 0,
        size: 100,
      });
    requests
      .expectOne((r) => r.url === `${api}/curso/pagina`)
      .flush({
        content: [{ id_curso: 3, nombre_curso: 'Matemática' }],
        totalElements: 1,
        number: 0,
        size: 100,
      });
    expect(nombres.nombre('asignacion', 7).texto).toBe(
      'Matemática · Rosa Mamani',
    );
  });
});

describe('formato', () => {
  it('escribe fechas y horas en formato peruano', () => {
    expect(fechaPeruana('2026-08-25')).toBe('25/08/2026');
    expect(fechaPeruana('2026-08-25T14:30:00')).toBe('25/08/2026 14:30');
    expect(horaPeruana('14:30:00')).toBe('14:30');
    expect(horaPeruana('9:05')).toBe('09:05');
    expect(fechaPeruana(null)).toBe('');
  });
  it('convierte los valores en mayúsculas en texto legible', () => {
    expect(textoLegible('EN_PROCESO')).toBe('En proceso');
    expect(textoLegible('CRITICA')).toBe('Crítica');
    expect(textoLegible('Primaria')).toBe('Primaria');
  });
  it('asigna el tono de cada estado y prioridad', () => {
    expect(tonoDe('RESUELTA')).toBe('verde');
    expect(tonoDe('EN_PROCESO')).toBe('ambar');
    expect(tonoDe('ABIERTA')).toBe('azul');
    expect(tonoDe('CERRADA')).toBe('gris');
    expect(tonoDe('VENCIDO')).toBe('rojo');
    expect(tonoDe('CRITICA', 'prioridad')).toBe('critico');
    expect(tonoDe('ALTA', 'prioridad')).toBe('naranja');
    expect(tonoDe('MEDIA', 'prioridad')).toBe('ambar');
    expect(tonoDe('BAJA', 'prioridad')).toBe('salvia');
    expect(tonoDe('OTRO')).toBe('neutro');
  });
});
