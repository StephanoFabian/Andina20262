import { Component, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import {
  BarController,
  BarElement,
  CategoryScale,
  ChartConfiguration,
  Filler,
  LinearScale,
  LineController,
  LineElement,
  PointElement,
  Tooltip,
} from 'chart.js';
import { BaseChartDirective, provideCharts } from 'ng2-charts';
import { map } from 'rxjs';
import { ESTADOS_CONVENIO } from '../../../models/convenio';
import { ConvenioPorVencerDTO } from '../../../models/convenio-por-vencer';
import { ConvenioResumenEstadoDTO } from '../../../models/convenio-resumen-estado';
import { ConvenioVencimientoMesDTO } from '../../../models/convenio-vencimiento-mes';
import { Convenioservice } from '../../../services/convenioservice';
import {
  Consulta,
  ejecutarConsulta,
  SIN_CONSULTAR,
} from '../../compartido/consulta';
import { Chip } from '../../compartido/chip/chip';
import { EstadoVacio } from '../../compartido/estado-vacio/estado-vacio';
import { Kpi, TarjetasKpi } from '../../compartido/tarjetas-kpi/tarjetas-kpi';
import { FechaPePipe, textoLegible } from '../../compartido/formato';
import {
  AMBAR_ANDINA,
  FUENTE_GRAFICOS,
  GRIS_OTROS,
  LINEA_REJILLA,
  ROJO_ALERTA,
  TEXTO_SECUNDARIO,
  VERDES_ANDINA,
} from '../../compartido/paleta-andina';

/** Mismo significado que los chips de estado (formato.ts): verde, ámbar, rojo y gris */
const COLOR_ESTADO: Record<string, string> = {
  VIGENTE: VERDES_ANDINA[1],
  EN_RENOVACION: AMBAR_ANDINA,
  VENCIDO: ROJO_ALERTA,
  CANCELADO: GRIS_OTROS,
  FINALIZADO: GRIS_OTROS,
};

/** Ejes con la tipografía y los grises de la app */
const EJES = {
  y: {
    beginAtZero: true,
    ticks: { precision: 0, color: TEXTO_SECUNDARIO, font: FUENTE_GRAFICOS },
    grid: { color: LINEA_REJILLA },
    border: { display: false },
  },
  x: {
    grid: { display: false },
    ticks: { color: '#22372f', font: FUENTE_GRAFICOS },
    border: { color: LINEA_REJILLA },
  },
};

const numero = new Intl.NumberFormat('es-PE');
const soles = new Intl.NumberFormat('es-PE', {
  style: 'currency',
  currency: 'PEN',
  maximumFractionDigits: 0,
});

const nombreMes = new Intl.DateTimeFormat('es-PE', {
  month: 'short',
  year: 'numeric',
});

/** "2026-12" → "dic 2026" (sin Date en UTC: evita desfases de zona horaria) */
function mesLegible(mes: string): string {
  const m = /^(\d{4})-(\d{2})$/.exec(mes);
  if (!m) return mes;
  return nombreMes
    .format(new Date(Number(m[1]), Number(m[2]) - 1, 1))
    .replace('.', '');
}

/** Plazo del KPI y de la tabla de próximos vencimientos (el mismo valor inicial que el reporte por vencer) */
const PLAZO_DIAS = 90;
/** Meses del gráfico de línea */
const MESES = 12;

/** Dashboard: convenios por estado, presupuesto y vencimientos de los próximos meses */
@Component({
  selector: 'app-convenio-dashboard',
  imports: [
    MatButtonModule,
    MatProgressBarModule,
    MatTableModule,
    EstadoVacio,
    TarjetasKpi,
    RouterLink,
    BaseChartDirective,
    Chip,
    FechaPePipe,
  ],
  providers: [
    provideCharts({
      registerables: [
        BarController,
        BarElement,
        CategoryScale,
        LinearScale,
        LineController,
        LineElement,
        PointElement,
        Filler,
        Tooltip,
      ],
    }),
  ],
  templateUrl: './convenio-dashboard.html',
})
export class ConvenioDashboard {
  private readonly cS = inject(Convenioservice);

  protected readonly columnas = [
    'nombre',
    'entidad',
    'fechaFin',
    'diasRestantes',
    'estado',
  ];
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

  protected readonly estados = computed(() => this.resumen().datos ?? []);

  private delEstado(estado: string) {
    return this.estados().find((e) => e.estado === estado);
  }

  protected readonly kpis = computed<Kpi[]>(() => {
    const total = this.estados().reduce((s, e) => s + e.cantidad, 0);
    const vigentes = this.delEstado('VIGENTE');
    const enRenovacion = this.delEstado('EN_RENOVACION');
    const proximos = this.totalProximos();
    return [
      {
        etiqueta: 'Convenios registrados',
        valor: numero.format(total),
        detalle: `En ${numero.format(this.estados().length)} estado(s)`,
        alerta: false,
      },
      {
        etiqueta: 'Vigentes',
        valor: numero.format(vigentes?.cantidad ?? 0),
        detalle: `${numero.format(enRenovacion?.cantidad ?? 0)} más en renovación`,
        alerta: false,
      },
      {
        etiqueta: 'Presupuesto vigente',
        valor: soles.format(vigentes?.presupuesto ?? 0),
        detalle: 'Suma de los convenios vigentes',
        alerta: false,
      },
      {
        etiqueta: `Vencidos o por vencer (${PLAZO_DIAS} días)`,
        valor: numero.format(proximos),
        detalle: proximos > 0 ? 'Revisar su renovación' : 'Ninguno requiere atención',
        alerta: proximos > 0,
      },
    ];
  });

  /** "EN_RENOVACION" → "En renovación" */
  protected nombreEstado(estado: string): string {
    return ESTADOS_CONVENIO[estado] ?? textoLegible(estado);
  }

  protected readonly datosEstados = computed<ChartConfiguration<'bar'>['data']>(
    () => ({
      labels: this.estados().map((e) => this.nombreEstado(e.estado)),
      datasets: [
        {
          label: 'Convenios',
          data: this.estados().map((e) => e.cantidad),
          backgroundColor: this.estados().map(
            (e) => COLOR_ESTADO[e.estado] ?? VERDES_ANDINA[3],
          ),
          borderRadius: 4,
          maxBarThickness: 48,
        },
      ],
    }),
  );

  protected readonly opcionesEstados: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    scales: EJES,
    plugins: {
      tooltip: {
        callbacks: {
          label: (ctx) => {
            const e = this.estados()[ctx.dataIndex];
            return e
              ? ` ${numero.format(e.cantidad)} convenio(s) · ${soles.format(e.presupuesto)}`
              : '';
          },
        },
      },
    },
  };

  protected readonly meses12 = computed(() => this.porMes().datos ?? []);
  protected readonly totalMeses = computed(() =>
    this.meses12().reduce((s, m) => s + m.cantidad, 0),
  );

  protected readonly datosMeses = computed<ChartConfiguration<'line'>['data']>(
    () => ({
      labels: this.meses12().map((m) => mesLegible(m.mes)),
      datasets: [
        {
          label: 'Convenios que vencen',
          data: this.meses12().map((m) => m.cantidad),
          borderColor: VERDES_ANDINA[1],
          borderWidth: 2,
          /* Área suave en --mint bajo la línea */
          backgroundColor: 'rgba(234, 242, 233, 0.7)',
          fill: 'origin',
          tension: 0.25,
          pointRadius: 4,
          pointHoverRadius: 6,
          pointBackgroundColor: '#fff',
          pointBorderColor: VERDES_ANDINA[1],
          pointBorderWidth: 2,
        },
      ],
    }),
  );

  protected readonly opcionesMeses: ChartConfiguration<'line'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      ...EJES,
      x: {
        ...EJES.x,
        /* En celular no caben 12 meses: se muestran en horizontal y se saltan algunos */
        ticks: { ...EJES.x.ticks, maxRotation: 0, autoSkip: true, autoSkipPadding: 12 },
      },
    },
    plugins: {
      tooltip: {
        callbacks: {
          label: (ctx) => {
            const m = this.meses12()[ctx.dataIndex];
            return m
              ? ` ${numero.format(m.cantidad)} convenio(s) · ${soles.format(m.presupuesto)}`
              : '';
          },
        },
      },
    },
  };

  constructor() {
    this.cargar();
  }

  protected cargar(): void {
    if (this.cargando() > 0) return;
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
