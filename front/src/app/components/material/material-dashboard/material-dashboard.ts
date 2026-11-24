import { Component, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { MaterialPorTipoDTO } from '../../../models/material-por-tipo';
import { Materialservice } from '../../../services/materialservice';
import {
  Consulta,
  ejecutarConsulta,
  SIN_CONSULTAR,
} from '../../compartido/consulta';

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
  imports: [MatButtonModule, RouterLink],
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
