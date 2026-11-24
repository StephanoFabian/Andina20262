import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';

/** Dashboard: distribución de los materiales por tipo (KPI, gráfico y detalle) */
@Component({
  selector: 'app-material-dashboard',
  imports: [MatButtonModule, RouterLink],
  templateUrl: './material-dashboard.html',
})
export class MaterialDashboard {}
