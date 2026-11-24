import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';

/** Dashboard: cuántos materiales tiene cada curso y cuáles no tienen ninguno */
@Component({
  selector: 'app-materialcurso-dashboard',
  imports: [MatButtonModule, RouterLink],
  templateUrl: './materialcurso-dashboard.html',
})
export class MaterialcursoDashboard {}
