import { Component, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import {
  BarController,
  BarElement,
  CategoryScale,
  ChartConfiguration,
  LinearScale,
  Tooltip,
} from 'chart.js';
import { BaseChartDirective, provideCharts } from 'ng2-charts';
import { CursoMaterialesDTO } from '../../../models/curso-materiales';
import { Materialcursoservice } from '../../../services/materialcursoservice';
import {
  Consulta,
  ejecutarConsulta,
  SIN_CONSULTAR,
} from '../../compartido/consulta';
import {
  AMBAR_ANDINA,
  FUENTE_GRAFICOS,
  LINEA_REJILLA,
  ROJO_ALERTA,
  TEXTO_SECUNDARIO,
  VERDES_ANDINA,
} from '../../compartido/paleta-andina';

/** Más de ~12 barras dejan de leerse: el gráfico muestra los 12 cursos con menos materiales */
const LIMITE_BARRAS = 12;

const numero = new Intl.NumberFormat('es-PE', { maximumFractionDigits: 1 });
const porcentaje = new Intl.NumberFormat('es-PE', {
  style: 'percent',
  maximumFractionDigits: 0,
});

/** Dashboard: cuántos materiales tiene cada curso y cuáles no tienen ninguno */
@Component({
  selector: 'app-materialcurso-dashboard',
  imports: [MatButtonModule, MatTableModule, RouterLink, BaseChartDirective],
  providers: [
    provideCharts({
      registerables: [
        BarController,
        BarElement,
        CategoryScale,
        LinearScale,
        Tooltip,
      ],
    }),
  ],
  templateUrl: './materialcurso-dashboard.html',
})
export class MaterialcursoDashboard {
  private readonly mcS = inject(Materialcursoservice);

  protected readonly resultado =
    signal<Consulta<CursoMaterialesDTO>>(SIN_CONSULTAR);
  protected readonly cargando = signal(0);
  protected readonly error = signal<string | null>(null);

  /** Cursos de menos a más materiales (el backend ya los ordena así) */
  protected readonly cursos = computed(() => this.resultado().datos ?? []);

  protected readonly sinMateriales = computed(
    () => this.cursos().filter((c) => c.cantidadMateriales === 0).length,
  );

  protected readonly kpis = computed(() => {
    const cursos = this.cursos();
    const total = cursos.length;
    const materiales = cursos.reduce((s, c) => s + c.cantidadMateriales, 0);
    const sin = this.sinMateriales();
    return [
      {
        etiqueta: 'Cursos',
        valor: numero.format(total),
        detalle: `${numero.format(materiales)} asignación(es) de materiales`,
        alerta: false,
      },
      {
        etiqueta: 'Cursos sin materiales',
        valor: numero.format(sin),
        detalle: sin > 0 ? 'Prioridad: subirles material' : 'Todos tienen al menos uno',
        alerta: sin > 0,
      },
      {
        etiqueta: 'Promedio por curso',
        valor: numero.format(total > 0 ? materiales / total : 0),
        detalle: 'Materiales por curso',
        alerta: false,
      },
      {
        etiqueta: 'Cursos con material',
        valor: porcentaje.format(total > 0 ? (total - sin) / total : 0),
        detalle: `${numero.format(total - sin)} de ${numero.format(total)} cursos`,
        alerta: false,
      },
    ];
  });

  protected readonly columnas = ['curso', 'cantidadMateriales'];
  protected readonly limite = LIMITE_BARRAS;
  protected readonly visibles = computed(() =>
    this.cursos().slice(0, LIMITE_BARRAS),
  );
  /** 28 px por barra más el eje: el gráfico crece con las barras */
  protected readonly altoGrafico = computed(
    () => Math.max(160, this.visibles().length * 28 + 48) + 'px',
  );

  protected readonly datosBarras = computed<ChartConfiguration<'bar'>['data']>(
    () => ({
      /* Un curso sin materiales no tiene barra: su nombre lo marca */
      labels: this.visibles().map((c) =>
        c.cantidadMateriales === 0 ? `${c.curso} (sin materiales)` : c.curso,
      ),
      datasets: [
        {
          label: 'Materiales',
          data: this.visibles().map((c) => c.cantidadMateriales),
          /* Un solo material: ámbar (atención); dos o más: verde Andina */
          backgroundColor: this.visibles().map((c) =>
            c.cantidadMateriales <= 1 ? AMBAR_ANDINA : VERDES_ANDINA[1],
          ),
          hoverBackgroundColor: this.visibles().map((c) =>
            c.cantidadMateriales <= 1 ? '#975f12' : VERDES_ANDINA[0],
          ),
          borderRadius: 4,
          maxBarThickness: 20,
        },
      ],
    }),
  );

  protected readonly opcionesBarras: ChartConfiguration<'bar'>['options'] = {
    indexAxis: 'y',
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      x: {
        beginAtZero: true,
        ticks: { precision: 0, color: TEXTO_SECUNDARIO, font: FUENTE_GRAFICOS },
        grid: { color: LINEA_REJILLA },
        border: { display: false },
        title: {
          display: true,
          text: 'Materiales',
          color: TEXTO_SECUNDARIO,
          font: FUENTE_GRAFICOS,
        },
      },
      y: {
        grid: { display: false },
        border: { color: LINEA_REJILLA },
        ticks: {
          font: FUENTE_GRAFICOS,
          color: (ctx) =>
            this.visibles()[ctx.index]?.cantidadMateriales === 0
              ? ROJO_ALERTA
              : '#22372f',
        },
      },
    },
    plugins: {
      tooltip: {
        callbacks: {
          label: (ctx) => ` ${numero.format(ctx.parsed.x ?? 0)} material(es)`,
        },
      },
    },
  };

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
