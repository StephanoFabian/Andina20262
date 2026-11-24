import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { map } from 'rxjs';
import { ConvenioPorVencerDTO } from '../../../models/convenio-por-vencer';
import { ConvenioResumenEstadoDTO } from '../../../models/convenio-resumen-estado';
import { ConvenioVencimientoMesDTO } from '../../../models/convenio-vencimiento-mes';
import { Convenioservice } from '../../../services/convenioservice';
import {
  Consulta,
  ejecutarConsulta,
  SIN_CONSULTAR,
} from '../../compartido/consulta';

/** Plazo del KPI y de la tabla de próximos vencimientos (el mismo valor inicial que el reporte por vencer) */
const PLAZO_DIAS = 90;
/** Meses del gráfico de línea */
const MESES = 12;

/** Dashboard: convenios por estado, presupuesto y vencimientos de los próximos meses */
@Component({
  selector: 'app-convenio-dashboard',
  imports: [MatButtonModule, RouterLink],
  templateUrl: './convenio-dashboard.html',
})
export class ConvenioDashboard {
  private readonly cS = inject(Convenioservice);

  protected readonly plazo = PLAZO_DIAS;
  protected readonly meses = MESES;

  protected readonly resumen =
    signal<Consulta<ConvenioResumenEstadoDTO>>(SIN_CONSULTAR);
  protected readonly porMes =
    signal<Consulta<ConvenioVencimientoMesDTO>>(SIN_CONSULTAR);
  protected readonly proximos =
    signal<Consulta<ConvenioPorVencerDTO>>(SIN_CONSULTAR);
  /** Total de convenios vencidos o que vencen en PLAZO_DIAS (la tabla muestra solo los primeros) */
  protected readonly totalProximos = signal(0);

  protected readonly cargando = signal(0);
  protected readonly error = signal<string | null>(null);

  constructor() {
    this.cargar();
  }

  protected cargar(): void {
    ejecutarConsulta(
      this.cS.resumenPorEstado(),
      this.resumen,
      this.cargando,
      this.error,
      'Todavía no hay convenios registrados.',
    );
    ejecutarConsulta(
      this.cS.vencimientosPorMes(MESES),
      this.porMes,
      this.cargando,
      this.error,
    );
    ejecutarConsulta(
      this.cS.reportePorVencer(PLAZO_DIAS, 0, 10).pipe(
        map((p) => {
          this.totalProximos.set(p.totalElements);
          return p.content;
        }),
      ),
      this.proximos,
      this.cargando,
      this.error,
      `Ningún convenio vence en los próximos ${PLAZO_DIAS} días.`,
    );
  }
}
