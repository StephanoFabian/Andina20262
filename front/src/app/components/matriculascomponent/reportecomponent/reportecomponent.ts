import { DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { EstudianteVariasMatriculas } from '../../../models/EstudianteVariasMatriculas';
import { MatriculasPorColegio } from '../../../models/MatriculasPorColegio';
import { MatriculaService } from '../../../services/matriculas.service';
import {
  Consulta,
  SIN_CONSULTAR,
  ejecutarConsulta,
} from '../../compartido/consulta';
import {
  Barra,
  GraficoBarras,
} from '../../compartido/grafico-barras/grafico-barras';

/** Reportes: matrículas por colegio y estudiantes con más de una matrícula */
@Component({
  selector: 'app-matriculas-reporte',
  imports: [
    MatTableModule,
    MatProgressBarModule,
    MatButtonModule,
    RouterLink,
    DecimalPipe,
    GraficoBarras,
  ],
  templateUrl: './reportecomponent.html',
})
export class MatriculaReporteComponent implements OnInit {
  private readonly service = inject(MatriculaService);

  protected readonly cargando = signal(0);
  protected readonly errorColegio = signal<string | null>(null);
  protected readonly errorVarias = signal<string | null>(null);

  protected readonly porColegio =
    signal<Consulta<MatriculasPorColegio>>(SIN_CONSULTAR);
  protected readonly columnasColegio = [
    'colegio',
    'departamento',
    'tipoZona',
    'totalMatriculas',
    'porcentajeDelTotal',
  ];
  protected readonly barrasColegio = computed<Barra[]>(() =>
    (this.porColegio().datos ?? []).map((r) => ({
      etiqueta: r.colegio,
      valor: r.totalMatriculas,
    })),
  );

  protected readonly variasMatriculas =
    signal<Consulta<EstudianteVariasMatriculas>>(SIN_CONSULTAR);
  protected readonly columnasVarias = [
    'idPersona',
    'nombres',
    'apellidos',
    'matriculas',
    'colegios',
  ];
  protected readonly barrasVarias = computed<Barra[]>(() =>
    (this.variasMatriculas().datos ?? []).map((r) => ({
      etiqueta: `${r.nombres} ${r.apellidos}`,
      valor: r.matriculas,
    })),
  );

  ngOnInit(): void {
    ejecutarConsulta(
      this.service.reportePorColegio(),
      this.porColegio,
      this.cargando,
      this.errorColegio,
      'No hay matrículas registradas.',
    );
    ejecutarConsulta(
      this.service.reporteVariasMatriculas(),
      this.variasMatriculas,
      this.cargando,
      this.errorVarias,
      'Ningún estudiante tiene más de una matrícula.',
    );
  }
}
