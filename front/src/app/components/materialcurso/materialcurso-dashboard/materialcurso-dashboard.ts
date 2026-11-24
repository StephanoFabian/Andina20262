import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { CursoMaterialesDTO } from '../../../models/curso-materiales';
import { Materialcursoservice } from '../../../services/materialcursoservice';
import {
  Consulta,
  ejecutarConsulta,
  SIN_CONSULTAR,
} from '../../compartido/consulta';

/** Dashboard: cuántos materiales tiene cada curso y cuáles no tienen ninguno */
@Component({
  selector: 'app-materialcurso-dashboard',
  imports: [MatButtonModule, RouterLink],
  templateUrl: './materialcurso-dashboard.html',
})
export class MaterialcursoDashboard {
  private readonly mcS = inject(Materialcursoservice);

  protected readonly resultado =
    signal<Consulta<CursoMaterialesDTO>>(SIN_CONSULTAR);
  protected readonly cargando = signal(0);
  protected readonly error = signal<string | null>(null);

  constructor() {
    this.cargar();
  }

  protected cargar(): void {
    ejecutarConsulta(
      this.mcS.reporteCompletoMaterialesPorCurso(),
      this.resultado,
      this.cargando,
      this.error,
      'Todavía no hay cursos registrados.',
    );
  }
}
