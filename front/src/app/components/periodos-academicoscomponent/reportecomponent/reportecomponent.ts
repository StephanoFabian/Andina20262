import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { EvolucionPeriodo } from '../../../models/EvolucionPeriodo';
import { MatriculaTardia } from '../../../models/MatriculaTardia';
import { PeriodoAcademicoService } from '../../../services/periodos-academicos.service';
import {
  Consulta,
  SIN_CONSULTAR,
  ejecutarConsulta,
} from '../../compartido/consulta';
import {
  Barra,
  GraficoBarras,
} from '../../compartido/grafico-barras/grafico-barras';

/** Reportes: evolución de la matrícula por periodo y matrículas tardías */
import { Chip } from '../../compartido/chip/chip';
@Component({
  selector: 'app-periodos-academicos-reporte',
  imports: [
    Chip,
    MatTableModule,
    MatProgressBarModule,
    MatButtonModule,
    RouterLink,
    DatePipe,
    DecimalPipe,
    GraficoBarras,
  ],
  templateUrl: './reportecomponent.html',
})
export class PeriodoAcademicoReporteComponent implements OnInit {
  private readonly service = inject(PeriodoAcademicoService);

  protected readonly cargando = signal(0);
  protected readonly errorEvolucion = signal<string | null>(null);
  protected readonly errorTardia = signal<string | null>(null);

  protected readonly evolucion =
    signal<Consulta<EvolucionPeriodo>>(SIN_CONSULTAR);
  protected readonly columnasEvolucion = [
    'periodo',
    'fechaInicio',
    'estado',
    'matriculados',
    'periodoAnterior',
    'variacionPorcentual',
  ];
  protected readonly barrasEvolucion = computed<Barra[]>(() =>
    (this.evolucion().datos ?? []).map((r) => ({
      etiqueta: r.periodo,
      valor: r.matriculados,
    })),
  );

  protected readonly tardia = signal<Consulta<MatriculaTardia>>(SIN_CONSULTAR);
  protected readonly columnasTardia = [
    'periodo',
    'fechaInicio',
    'totalMatriculas',
    'anticipadas',
    'tardias',
    'fueraDelPeriodo',
    'porcentajeTardias',
  ];
  protected readonly barrasTardia = computed<Barra[]>(() =>
    (this.tardia().datos ?? []).map((r) => ({
      etiqueta: r.periodo,
      valor: r.porcentajeTardias,
    })),
  );

  ngOnInit(): void {
    ejecutarConsulta(
      this.service.reporteEvolucion(),
      this.evolucion,
      this.cargando,
      this.errorEvolucion,
      'No hay periodos académicos registrados.',
    );
    ejecutarConsulta(
      this.service.reporteMatriculaTardia(),
      this.tardia,
      this.cargando,
      this.errorTardia,
      'No hay matrículas para analizar.',
    );
  }
}
