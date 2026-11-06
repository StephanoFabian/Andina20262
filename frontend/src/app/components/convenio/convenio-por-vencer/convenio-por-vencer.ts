import { OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { PageEvent } from '@angular/material/paginator';
import { mensajeError as errorPeticion } from '../../../services/errores';
import { CurrencyPipe, DatePipe } from '@angular/common';

import { Component, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import {
  MatPaginatorIntl,
  MatPaginatorModule,
} from '@angular/material/paginator';
import { PaginadorIntl } from '../../compartido/paginador-intl';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { ConvenioPorVencerDTO } from '../../../models/convenio-por-vencer';
import { ESTADOS_CONVENIO } from '../../../models/convenio';
import { Convenioservice } from '../../../services/convenioservice';

import { entero, mensajeCampo } from '../../compartido/validadores';

/** Reporte: convenios vencidos y los que vencen en los próximos N días */
@Component({
  selector: 'app-convenio-por-vencer',
  imports: [
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    RouterLink,
    DatePipe,
    CurrencyPipe,
  ],
  providers: [{ provide: MatPaginatorIntl, useClass: PaginadorIntl }],
  templateUrl: './convenio-por-vencer.html',
})
export class ConvenioPorVencer implements OnInit {
  private readonly cS = inject(Convenioservice);

  protected readonly estados = ESTADOS_CONVENIO;
  protected readonly dias = new FormControl(90, {
    nonNullable: true,
    validators: [
      Validators.required,
      entero,
      Validators.min(0),
      Validators.max(3650),
    ],
  });

  protected readonly columnas = [
    'nombre',
    'entidad',
    'region',
    'fechaFin',
    'diasRestantes',
    'presupuesto',
    'situacion',
  ];
  protected readonly aviso = signal<string | null>(null);

  protected errorDias(): string {
    return mensajeCampo(this.dias);
  }

  protected obtenerPagina() {
    return this.cS.reportePorVencer(this.dias.value, this.page, this.size);
  }
  protected consultar(): void {
    if (this.dias.invalid) {
      this.dias.markAsTouched();
      return;
    }
    this.page = 0;
    this.cargar();
  }

  /** Vencidos o con 30 días o menos se resaltan */
  protected claseDias(dias: number): string {
    return dias <= 30 ? 'estado-alerta' : 'estado-neutro';
  }

  protected readonly dataSource = new MatTableDataSource<ConvenioPorVencerDTO>(
    [],
  );
  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly total = signal(0);
  protected page = 0;
  protected size = 10;
  protected q = '';
  ngOnInit(): void {
    this.cargar();
  }
  protected cargar(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.obtenerPagina().subscribe({
      next: (p) => {
        this.dataSource.data = p.content;
        this.total.set(p.totalElements);
        this.cargando.set(false);
        if (this.page > 0 && !p.content.length) {
          this.page--;
          this.cargar();
        }
      },
      error: (err) => {
        this.cargando.set(false);
        this.error.set(errorPeticion(err));
      },
    });
  }
  protected paginar(e: PageEvent): void {
    this.page = e.pageIndex;
    this.size = e.pageSize;
    this.cargar();
  }
  protected buscar(texto: string): void {
    this.q = texto.trim().slice(0, 100);
    this.page = 0;
    this.cargar();
  }
}
