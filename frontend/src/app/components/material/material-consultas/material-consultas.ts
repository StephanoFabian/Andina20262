import { ReferenciaComponent } from '../../../shared/referencia.component';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { map } from 'rxjs';
import { Component, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';
import { RouterLink } from '@angular/router';
import { MaterialDTO } from '../../../models/material';

import { Materialservice } from '../../../services/materialservice';
import {
  Consulta,
  ejecutarConsulta,
  SIN_CONSULTAR,
} from '../../compartido/consulta';
import { mensajeCampo, noVacio } from '../../compartido/validadores';
import { MaterialTabla } from '../material-tabla/material-tabla';

/** Consultas para todos los roles: materiales por título y materiales de un curso */
@Component({
  selector: 'app-material-consultas',
  imports: [
    ReactiveFormsModule,
    ReferenciaComponent,
    MatPaginatorModule,
    MatTabsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatProgressBarModule,
    RouterLink,
    MaterialTabla,
  ],
  templateUrl: './material-consultas.html',
})
export class MaterialConsultas {
  private readonly mS = inject(Materialservice);

  protected readonly cargando = signal(0);
  protected readonly error = signal<string | null>(null);

  protected readonly titulo = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, noVacio, Validators.maxLength(100)],
  });
  protected readonly porTitulo = signal<Consulta<MaterialDTO>>(SIN_CONSULTAR);

  protected readonly idCurso = new FormControl<number | null>(
    null,
    Validators.required,
  );
  protected readonly porCurso = signal<Consulta<MaterialDTO>>(SIN_CONSULTAR);

  protected totalTitulo = signal(0);
  protected totalCurso = signal(0);
  protected paginaTitulo = 0;
  protected paginaCurso = 0;
  protected sizeTitulo = 10;
  protected sizeCurso = 10;
  private ultimoTitulo = '';
  private ultimoCurso: number | null = null;
  protected paginarTitulo(e: PageEvent) {
    this.paginaTitulo = e.pageIndex;
    this.sizeTitulo = e.pageSize;
    this.buscarPorTitulo(false);
  }
  protected paginarCurso(e: PageEvent) {
    this.paginaCurso = e.pageIndex;
    this.sizeCurso = e.pageSize;
    this.buscarPorCurso(false);
  }

  protected errorTitulo(): string {
    return mensajeCampo(this.titulo);
  }

  protected buscarPorTitulo(reiniciar = true): void {
    if (this.titulo.invalid) {
      this.titulo.markAsTouched();
      return;
    }
    if (this.cargando() > 0) return;
    const texto = this.titulo.value.trim();
    if (reiniciar || texto !== this.ultimoTitulo) this.paginaTitulo = 0;
    this.ultimoTitulo = texto;
    ejecutarConsulta(
      this.mS.consulta(this.paginaTitulo, this.sizeTitulo, texto).pipe(
        map((p) => {
          this.totalTitulo.set(p.totalElements);
          return p.content;
        }),
      ),
      this.porTitulo,
      this.cargando,
      this.error,
      `Ningún material comienza con "${texto}" en el título.`,
    );
  }

  protected buscarPorCurso(reiniciar = true): void {
    if (this.idCurso.invalid || this.idCurso.value === null) {
      this.idCurso.markAsTouched();
      return;
    }
    if (this.cargando() > 0) return;
    if (reiniciar || this.idCurso.value !== this.ultimoCurso)
      this.paginaCurso = 0;
    this.ultimoCurso = this.idCurso.value;
    ejecutarConsulta(
      this.mS
        .consulta(this.paginaCurso, this.sizeCurso, '', this.idCurso.value)
        .pipe(
          map((p) => {
            this.totalCurso.set(p.totalElements);
            return p.content;
          }),
        ),
      this.porCurso,
      this.cargando,
      this.error,
      'El curso no tiene materiales asociados.',
    );
  }
}
