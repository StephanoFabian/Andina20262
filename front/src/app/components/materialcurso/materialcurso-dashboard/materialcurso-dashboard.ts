import { Component, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { CursoMaterialesDTO } from '../../../models/curso-materiales';
import { Materialcursoservice } from '../../../services/materialcursoservice';
import {
  Consulta,
  ejecutarConsulta,
  SIN_CONSULTAR,
} from '../../compartido/consulta';

const numero = new Intl.NumberFormat('es-PE', { maximumFractionDigits: 1 });
const porcentaje = new Intl.NumberFormat('es-PE', {
  style: 'percent',
  maximumFractionDigits: 0,
});

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
