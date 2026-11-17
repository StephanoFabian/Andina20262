import { Component, computed, inject, signal } from '@angular/core';
import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
  NavigationEnd,
} from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from './core/auth.service';
import { esEstudiante, modulosVisibles } from './services/permisos';
import { SesionUsuario } from './components/compartido/sesion-usuario';
import { iconoModulo } from './components/compartido/iconos';
@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatIconModule,
    MatButtonModule,
  ],
  templateUrl: './app.html',
})
export class App {
  readonly auth = inject(AuthService);
  readonly router = inject(Router);
  readonly abierto = signal(false);
  /** Solo los módulos que corresponden al rol (permisos.ts) */
  readonly modulos = computed(() => modulosVisibles(this.auth.roles()));
  readonly tituloMenu = computed(() =>
    esEstudiante(this.auth.roles()) ? 'MIS CLASES' : 'GESTIÓN ACADÉMICA',
  );
  readonly usuario = inject(SesionUsuario);
  readonly icono = iconoModulo;
  constructor() {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) this.abierto.set(false);
    });
  }
  /** Enlace "Saltar al contenido": mueve el foco sin cambiar la ruta */
  irAlContenido(evento: Event): void {
    evento.preventDefault();
    document.getElementById('contenido')?.focus();
  }
  salir(): void {
    this.auth.logout();
    void this.router.navigate(['/']);
  }
}
