import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../core/auth.service';
import { MODULOS } from '../../models/catalogo';
@Component({
  selector: 'app-home',
  imports: [RouterLink, MatIconModule],
  templateUrl: './homecomponent.html',
})
export class HomeComponent {
  readonly auth = inject(AuthService);
  readonly modulos = MODULOS;
}
