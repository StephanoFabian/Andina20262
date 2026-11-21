import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { Modulo } from '../models/modulo';
interface Claims {
  sub: string;
  exp: number;
  roles: string[];
}
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenState = signal<string | null>(this.read());
  readonly claims = computed(() => this.decode(this.tokenState()));
  readonly roles = computed(() =>
    (this.claims()?.roles || []).map((r) => r.replace(/^ROLE_/, '')),
  );
  constructor(private http: HttpClient) {}
  private read(): string | null {
    try {
      return sessionStorage.getItem('andina.token');
    } catch {
      return null;
    }
  }
  private decode(token: string | null): Claims | null {
    try {
      if (!token) return null;
      const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const claims = JSON.parse(atob(payload)) as Claims;
      return typeof claims.exp === 'number' &&
        typeof claims.sub === 'string' &&
        Array.isArray(claims.roles) &&
        claims.roles.every((r) => typeof r === 'string')
        ? claims
        : null;
    } catch {
      return null;
    }
  }
  token(): string | null {
    return this.valid() ? this.tokenState() : null;
  }
  valid(): boolean {
    const c = this.claims();
    return !!c && c.exp * 1000 > Date.now();
  }
  login(idPersona: number, passwordPersona: string) {
    return this.http
      .post<{ token: string }>(`${environment.base}/login`, {
        idPersona,
        passwordPersona,
      })
      .pipe(
        tap((result) => {
          const claims = this.decode(result.token);
          if (!claims || claims.exp * 1000 <= Date.now())
            throw new Error('La API devolvió un token inválido');
          sessionStorage.setItem('andina.token', result.token);
          this.tokenState.set(result.token);
        }),
      );
  }
  logout(): void {
    try {
      sessionStorage.removeItem('andina.token');
    } finally {
      this.tokenState.set(null);
    }
  }
  puedeEscribir(config: Modulo): boolean {
    return this.valid() && config.roles.some((r) => this.roles().includes(r));
  }
}
