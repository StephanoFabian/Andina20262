import { Component, OnInit, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { filter, switchMap } from 'rxjs';
import { PeriodoAcademico } from '../../../models/PeriodoAcademico';
import { PeriodoAcademicoService } from '../../../services/periodos-academicos.service';
import { AuthService } from '../../../core/auth.service';
import { confirmar } from '../../compartido/confirmar-dialogo/confirmar-dialogo';
import { mensajeError } from '../../../services/errores';
@Component({
  selector: 'app-periodos-academicos-listar',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
  ],
  templateUrl: './listarcomponent.html',
})
export class PeriodoAcademicoListarComponent implements OnInit {
  private readonly service = inject(PeriodoAcademicoService);
  private readonly auth = inject(AuthService);
  private readonly dialog = inject(MatDialog);
  private readonly snack = inject(MatSnackBar);
  readonly dataSource = new MatTableDataSource<PeriodoAcademico>([]);
  readonly displayedColumns = [
    'idPeriodo',
    'nombre',
    'fechaInicio',
    'fechaFin',
    'estado',
    'acciones',
  ];
  readonly busqueda = new FormControl('', { nonNullable: true });
  readonly cargando = signal(false);
  readonly error = signal('');
  readonly total = signal(0);
  page = 0;
  size = 10;
  ngOnInit(): void {
    this.cargar();
  }
  puedeEscribir(): boolean {
    return ['ADMIN', 'ADMIN_ESCUELA', 'ESPECIALISTA', 'LOCAL'].some((rol) =>
      this.auth.roles().includes(rol),
    );
  }
  cargar(): void {
    this.cargando.set(true);
    this.error.set('');
    this.service
      .pagina(this.page, this.size, this.busqueda.value.trim())
      .subscribe({
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
          this.error.set(mensajeError(err));
          this.cargando.set(false);
        },
      });
  }
  buscar(): void {
    this.page = 0;
    this.cargar();
  }
  paginar(e: PageEvent): void {
    this.page = e.pageIndex;
    this.size = e.pageSize;
    this.cargar();
  }
  eliminar(dto: PeriodoAcademico): void {
    confirmar(this.dialog, {
      titulo: 'Eliminar registro',
      mensaje: '¿Eliminar el registro seleccionado?',
    })
      .pipe(
        filter(Boolean),
        switchMap(() => this.service.eliminar(dto)),
      )
      .subscribe({
        next: () => {
          this.snack.open('Registro eliminado', 'Cerrar', { duration: 3500 });
          this.cargar();
        },
        error: (err) => this.error.set(mensajeError(err)),
      });
  }
}
