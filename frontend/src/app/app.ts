import { Component, inject, signal } from '@angular/core';
import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
  NavigationEnd,
} from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthService } from './core/auth.service';
import { MODULOS } from './models/catalogo';
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
  readonly modulos = MODULOS;
  constructor() {
    this.router.events.pipe(takeUntilDestroyed()).subscribe((event) => {
      if (event instanceof NavigationEnd) this.abierto.set(false);
    });
  }
  salir(): void {
    this.auth.logout();
    void this.router.navigate(['/login']);
  }
}
