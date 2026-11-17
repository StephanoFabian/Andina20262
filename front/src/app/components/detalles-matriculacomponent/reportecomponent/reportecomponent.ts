import { DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { AulasPorGrado } from '../../../models/AulasPorGrado';
import { PeriodoAcademico } from '../../../models/PeriodoAcademico';
import { RetencionColegio } from '../../../models/RetencionColegio';
import { RetiroPorCurso } from '../../../models/RetiroPorCurso';
import { DetalleMatriculaService } from '../../../services/detalles-matricula.service';
import { mensajeError } from '../../../services/errores';
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
import { mensajeCampo } from '../../compartido/validadores';

/** Reportes: retiro por curso, aulas necesarias por grado y retención por colegio */
@Component({
  selector: 'app-detalles-matricula-reporte',
  imports: [
    ReactiveFormsModule,
    MatTableModule,
    MatProgressBarModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonModule,
    RouterLink,
    DecimalPipe,
    GraficoBarras,
  ],
  templateUrl: './reportecomponent.html',
})
export class DetalleMatriculaReporteComponent implements OnInit {
  private readonly service = inject(DetalleMatriculaService);
  private readonly sPeriodo = inject(PeriodoAcademicoService);

  protected readonly cargando = signal(0);
  protected readonly errorRetiro = signal<string | null>(null);
  protected readonly errorAulas = signal<string | null>(null);
  protected readonly errorRetencion = signal<string | null>(null);

  /** Periodos para los selectores (el backend admite hasta 100 por página) */
  protected readonly periodos = signal<PeriodoAcademico[]>([]);
  protected readonly errorPeriodos = signal<string | null>(null);

  // Retiro por curso
  protected readonly retiro = signal<Consulta<RetiroPorCurso>>(SIN_CONSULTAR);
  protected readonly columnasRetiro = [
    'curso',
    'area',
    'totalMatriculados',
    'retirados',
    'trasladados',
    'porcentajePerdida',
  ];
  protected readonly barrasRetiro = computed<Barra[]>(() =>
    (this.retiro().datos ?? []).map((r) => ({
      etiqueta: r.curso,
      valor: r.porcentajePerdida,
    })),
  );

  // Aulas necesarias por grado
  protected readonly periodoAulas = new FormControl<number | null>(
    null,
    Validators.required,
  );
  protected readonly aulas = signal<Consulta<AulasPorGrado>>(SIN_CONSULTAR);
  protected readonly columnasAulas = [
    'grado',
    'nivel',
    'vigentes',
    'aulasNecesarias',
  ];
  protected readonly barrasAulas = computed<Barra[]>(() =>
    (this.aulas().datos ?? []).map((r) => ({
      etiqueta: `${r.grado} · ${r.nivel}`,
      valor: r.aulasNecesarias,
    })),
  );

  // Retención por colegio
  protected readonly periodoAnterior = new FormControl<number | null>(
    null,
    Validators.required,
  );
  protected readonly periodoActual = new FormControl<number | null>(
    null,
    Validators.required,
  );
  protected readonly avisoPeriodos = signal<string | null>(null);
  protected readonly retencion =
    signal<Consulta<RetencionColegio>>(SIN_CONSULTAR);
  protected readonly columnasRetencion = [
    'colegio',
    'alumnosAnterior',
    'alumnosActual',
    'siguieron',
    'seFueron',
    'nuevos',
    'variacion',
  ];
  protected readonly barrasRetencion = computed<Barra[]>(() =>
    (this.retencion().datos ?? []).map((r) => ({
      etiqueta: r.colegio,
      valor: r.seFueron,
    })),
  );

  ngOnInit(): void {
    this.sPeriodo.pagina(0, 100, '').subscribe({
      next: (p) => this.periodos.set(p.content),
      error: (err: unknown) => this.errorPeriodos.set(mensajeError(err)),
    });
    ejecutarConsulta(
      this.service.reporteRetiroPorCurso(),
      this.retiro,
      this.cargando,
      this.errorRetiro,
    );
  }

  protected errorCampo(control: FormControl<number | null>): string {
    return mensajeCampo(control);
  }

  protected consultarAulas(): void {
    const idPeriodo = this.periodoAulas.value;
    if (this.periodoAulas.invalid || idPeriodo === null) {
      this.periodoAulas.markAsTouched();
      return;
    }
    ejecutarConsulta(
      this.service.reporteAulasPorGrado(idPeriodo),
      this.aulas,
      this.cargando,
      this.errorAulas,
    );
  }

  protected consultarRetencion(): void {
    const anterior = this.periodoAnterior.value;
    const actual = this.periodoActual.value;
    if (
      this.periodoAnterior.invalid ||
      this.periodoActual.invalid ||
      anterior === null ||
      actual === null
    ) {
      this.periodoAnterior.markAsTouched();
      this.periodoActual.markAsTouched();
      return;
    }
    if (anterior === actual) {
      this.avisoPeriodos.set('Elige dos periodos distintos.');
      return;
    }
    this.avisoPeriodos.set(null);
    ejecutarConsulta(
      this.service.reporteRetencion(anterior, actual),
      this.retencion,
      this.cargando,
      this.errorRetencion,
    );
  }
}
