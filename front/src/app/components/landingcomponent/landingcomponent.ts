import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../core/auth.service';
@Component({
  selector: 'app-landing',
  imports: [RouterLink, MatIconModule],
  templateUrl: './landingcomponent.html',
})
export class LandingComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  constructor() {
    // Con sesión activa no tiene sentido ver la portada: va directo a su espacio.
    if (this.auth.valid()) void this.router.navigate(['/inicio']);
  }
}
