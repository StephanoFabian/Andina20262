import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';

/** Dashboard: convenios por estado, presupuesto y vencimientos de los próximos meses */
@Component({
  selector: 'app-convenio-dashboard',
  imports: [MatButtonModule, RouterLink],
  templateUrl: './convenio-dashboard.html',
})
export class ConvenioDashboard {}
