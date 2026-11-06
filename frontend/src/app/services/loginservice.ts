import { Injectable, computed, inject } from '@angular/core';
import { AuthService } from '../core/auth.service';
@Injectable({ providedIn: 'root' })
export class Loginservice {
  private readonly auth = inject(AuthService);
  readonly idPersona = computed(() => this.auth.claims()?.sub ?? null);
  tieneRol(roles: readonly string[]): boolean {
    return (
      this.auth.valid() && roles.some((r) => this.auth.roles().includes(r))
    );
  }
}
