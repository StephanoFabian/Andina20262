import { TestBed } from '@angular/core/testing';
import {
  HttpClient,
  provideHttpClient,
  withInterceptors,
} from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from './auth.service';

describe('Autenticación de peticiones', () => {
  let http: HttpClient;
  let requests: HttpTestingController;
  const logout = vi.fn();
  const navigate = vi.fn();
  beforeEach(() => {
    vi.clearAllMocks();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        {
          provide: AuthService,
          useValue: { token: () => 'token-de-prueba', logout },
        },
        { provide: Router, useValue: { navigate } },
      ],
    });
    http = TestBed.inject(HttpClient);
    requests = TestBed.inject(HttpTestingController);
  });
  afterEach(() => requests.verify());
  it('envía el token a nuestra API', () => {
    http.get('/backend/api/grados/pagina').subscribe();
    const req = requests.expectOne('/backend/api/grados/pagina');
    expect(req.request.headers.get('Authorization')).toBe(
      'Bearer token-de-prueba',
    );
    req.flush({ content: [] });
  });
  it('no entrega el token a un dominio ajeno', () => {
    http.get('https://example.test/backend/api/grados').subscribe();
    const req = requests.expectOne('https://example.test/backend/api/grados');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush({});
  });
  it('cierra una sesión rechazada por la API', () => {
    http.get('/backend/api/grados/pagina').subscribe({ error: () => {} });
    requests
      .expectOne('/backend/api/grados/pagina')
      .flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(logout).toHaveBeenCalledOnce();
    expect(navigate).toHaveBeenCalledWith(['/login']);
  });
  it('mantiene el login al rechazar una contraseña', () => {
    http.post('/backend/login', {}).subscribe({ error: () => {} });
    requests
      .expectOne('/backend/login')
      .flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(logout).not.toHaveBeenCalled();
  });
});
