import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { MaterialPorTipoDTO } from '../../../models/material-por-tipo';
import { Materialservice } from '../../../services/materialservice';
import {
  Consulta,
  ejecutarConsulta,
  SIN_CONSULTAR,
} from '../../compartido/consulta';

/** Dashboard: distribución de los materiales por tipo (KPI, gráfico y detalle) */
@Component({
  selector: 'app-material-dashboard',
  imports: [MatButtonModule, RouterLink],
  templateUrl: './material-dashboard.html',
})
export class MaterialDashboard {
  private readonly mS = inject(Materialservice);

  protected readonly resultado =
    signal<Consulta<MaterialPorTipoDTO>>(SIN_CONSULTAR);
  protected readonly cargando = signal(0);
  protected readonly error = signal<string | null>(null);

  constructor() {
    this.cargar();
  }

  protected cargar(): void {
    ejecutarConsulta(
      this.mS.reporteCompletoPorTipo(),
      this.resultado,
      this.cargando,
      this.error,
      'Todavía no hay materiales registrados.',
    );
  }
}
