import { Component, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import {
  ArcElement,
  ChartConfiguration,
  DoughnutController,
  Legend,
  Tooltip,
} from 'chart.js';
import { BaseChartDirective, provideCharts } from 'ng2-charts';
import { MaterialPorTipoDTO } from '../../../models/material-por-tipo';
import { Materialservice } from '../../../services/materialservice';
import {
  Consulta,
  ejecutarConsulta,
  SIN_CONSULTAR,
} from '../../compartido/consulta';
import {
  FUENTE_GRAFICOS,
  GRIS_OTROS,
  VERDES_ANDINA,
} from '../../compartido/paleta-andina';

const numero = new Intl.NumberFormat('es-PE');
const porcentaje = new Intl.NumberFormat('es-PE', {
  style: 'percent',
  maximumFractionDigits: 1,
});

/** Fila del reporte con el tipo ya escrito para mostrar */
interface TipoMaterial {
  tipo: string;
  cantidad: number;
  participacion: number;
}

/** "video" → "Video"; sin tipo → "Sin tipo" */
function nombreTipo(tipo: string | null): string {
  const t = (tipo ?? '').trim();
  return t ? t.charAt(0).toUpperCase() + t.slice(1) : 'Sin tipo';
}

/** Dashboard: distribución de los materiales por tipo (KPI, gráfico y detalle) */
@Component({
  selector: 'app-material-dashboard',
  imports: [MatButtonModule, MatTableModule, RouterLink, BaseChartDirective],
  providers: [
    provideCharts({
      registerables: [DoughnutController, ArcElement, Tooltip, Legend],
    }),
  ],
  templateUrl: './material-dashboard.html',
})
export class MaterialDashboard {
  private readonly mS = inject(Materialservice);

  protected readonly resultado =
    signal<Consulta<MaterialPorTipoDTO>>(SIN_CONSULTAR);
  protected readonly cargando = signal(0);
  protected readonly error = signal<string | null>(null);

  /** Tipos de mayor a menor cantidad, con su participación en el total */
  protected readonly tipos = computed<TipoMaterial[]>(() => {
    const datos = this.resultado().datos ?? [];
    const total = datos.reduce((s, r) => s + r.cantidad, 0);
    return datos
      .map((r) => ({
        tipo: nombreTipo(r.tipo),
        cantidad: r.cantidad,
        participacion: total > 0 ? r.cantidad / total : 0,
      }))
      .sort((a, b) => b.cantidad - a.cantidad || a.tipo.localeCompare(b.tipo));
  });

  protected readonly total = computed(() =>
    this.tipos().reduce((s, r) => s + r.cantidad, 0),
  );

  protected readonly kpis = computed(() => {
    const tipos = this.tipos();
    const mayor = tipos[0];
    const menor = tipos[tipos.length - 1];
    return [
      {
        etiqueta: 'Materiales registrados',
        valor: numero.format(this.total()),
        detalle: 'Suma de todos los tipos',
      },
      {
        etiqueta: 'Tipos de material',
        valor: numero.format(tipos.length),
        detalle: 'Incluye «Sin tipo» si hay materiales sin clasificar',
      },
      {
        etiqueta: 'Tipo más usado',
        valor: mayor?.tipo ?? '—',
        detalle: mayor
          ? `${numero.format(mayor.cantidad)} material(es) · ${porcentaje.format(mayor.participacion)}`
          : '',
      },
      {
        etiqueta: 'Tipo con menos materiales',
        valor: menor?.tipo ?? '—',
        detalle: menor
          ? `${numero.format(menor.cantidad)} material(es) · ${porcentaje.format(menor.participacion)}`
          : '',
      },
    ];
  });

  protected readonly columnas = ['tipo', 'cantidad', 'participacion'];
  protected readonly formatoNumero = (n: number) => numero.format(n);
  protected readonly formatoPorcentaje = (n: number) => porcentaje.format(n);

  /** Más de 6 porciones no se distinguen: los 5 tipos principales y el resto como «Otros» */
  protected readonly porciones = computed(() => {
    const tipos = this.tipos();
    if (tipos.length <= 6) return tipos.map((t) => ({ ...t, otros: false }));
    const resto = tipos.slice(5);
    return [
      ...tipos.slice(0, 5).map((t) => ({ ...t, otros: false })),
      {
        otros: true,
        tipo: `Otros (${resto.length} tipos)`,
        cantidad: resto.reduce((s, r) => s + r.cantidad, 0),
        participacion: resto.reduce((s, r) => s + r.participacion, 0),
      },
    ];
  });

  protected readonly datosDona = computed<ChartConfiguration<'doughnut'>['data']>(
    () => ({
      labels: this.porciones().map((p) => p.tipo),
      datasets: [
        {
          label: 'Materiales',
          data: this.porciones().map((p) => p.cantidad),
          backgroundColor: this.porciones().map((p, i) =>
            p.otros ? GRIS_OTROS : VERDES_ANDINA[i % VERDES_ANDINA.length],
          ),
          borderColor: '#fff',
          borderWidth: 2,
          hoverOffset: 6,
        },
      ],
    }),
  );

  protected readonly opcionesDona: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    cutout: '60%',
    plugins: {
      legend: {
        position: 'right',
        labels: {
          font: FUENTE_GRAFICOS,
          color: '#22372f',
          usePointStyle: true,
          pointStyle: 'circle',
          boxWidth: 10,
        },
      },
      tooltip: {
        callbacks: {
          label: (ctx) => {
            const p = this.porciones()[ctx.dataIndex];
            return p
              ? ` ${numero.format(p.cantidad)} material(es) · ${porcentaje.format(p.participacion)}`
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
    ejecutarConsulta(
      this.mS.reporteCompletoPorTipo(),
      this.resultado,
      this.cargando,
      this.error,
      'Todavía no hay materiales registrados.',
    );
  }
}
