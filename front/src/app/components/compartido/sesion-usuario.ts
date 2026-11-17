import { Injectable, computed, effect, inject, signal } from '@angular/core';
import { AuthService } from '../../core/auth.service';
import { Persona } from '../../models/Persona';
import { PersonaService } from '../../services/personas.service';
import { textoLegible } from './formato';

/**
 * Datos de presentación del usuario conectado (nombre, iniciales, rol legible).
 * El token solo trae el ID de la persona; el nombre se pide una vez con
 * GET /personas/{id}. Si falla, se sigue mostrando "Usuario #id".
 */
@Injectable({ providedIn: 'root' })
export class SesionUsuario {
  private readonly auth = inject(AuthService);
  private readonly personas = inject(PersonaService);
  private readonly persona = signal<Persona | null>(null);

  constructor() {
    effect((onCleanup) => {
      const id = Number(this.auth.claims()?.sub);
      this.persona.set(null);
      if (!this.auth.claims() || !Number.isFinite(id)) return;
      const sub = this.personas.listId(id).subscribe({
        next: (p) => this.persona.set(p ?? null),
        error: () => this.persona.set(null),
      });
      onCleanup(() => sub.unsubscribe());
    });
  }

  /** Nombres y apellidos, o "Usuario #id" si no se pudo obtener */
  readonly nombre = computed(() => {
    const p = this.persona();
    const completo = [p?.nombresPersona, p?.apellidosPersona]
      .filter((x) => !!x && x.trim() !== '')
      .join(' ')
      .trim();
    return completo || `Usuario #${this.auth.claims()?.sub ?? ''}`;
  });

  /** Primer nombre, para el saludo; vacío si no se conoce */
  readonly primerNombre = computed(
    () => this.persona()?.nombresPersona?.trim().split(/\s+/)[0] ?? '',
  );

  readonly iniciales = computed(() => {
    const p = this.persona();
    const letras = [p?.nombresPersona, p?.apellidosPersona]
      .map((x) => x?.trim().charAt(0) ?? '')
      .join('')
      .toUpperCase();
    return letras || this.auth.roles()[0]?.slice(0, 1) || 'U';
  });

  readonly rol = computed(() =>
    this.auth
      .roles()
      .map((r) => textoLegible(r))
      .join(' · '),
  );
}
