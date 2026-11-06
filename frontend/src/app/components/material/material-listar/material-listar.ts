import { OnInit, signal } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { PageEvent } from '@angular/material/paginator';
import { mensajeError as errorPeticion } from '../../../services/errores';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import {
  MatPaginatorIntl,
  MatPaginatorModule,
} from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { filter, switchMap } from 'rxjs';
import { MaterialDTO } from '../../../models/material';

import { map } from 'rxjs';
import { mensajeError } from '../../../services/errores';
import { Loginservice } from '../../../services/loginservice';
import { Materialservice } from '../../../services/materialservice';
import { modulo, PERMISOS_EXTRA } from '../../../services/permisos';

import { confirmar } from '../../compartido/confirmar-dialogo/confirmar-dialogo';
import { PaginadorIntl } from '../../compartido/paginador-intl';

interface FilaMaterial extends MaterialDTO {
  autor: string;
}

@Component({
  selector: 'app-material-listar',
  imports: [
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressBarModule,
    MatFormFieldModule,
    MatInputModule,
    RouterLink,
    DatePipe,
  ],
  providers: [{ provide: MatPaginatorIntl, useClass: PaginadorIntl }],
  templateUrl: './material-listar.html',
})
export class MaterialListar implements OnInit {
  private readonly mS = inject(Materialservice);
  private readonly dialog = inject(MatDialog);
  private readonly snack = inject(MatSnackBar);
  private readonly loginService = inject(Loginservice);

  private readonly permisos = modulo('material').permisos;
  protected readonly puedeInsertar = this.loginService.tieneRol(
    this.permisos.insertar,
  );
  protected readonly puedeModificar = this.loginService.tieneRol(
    this.permisos.modificar,
  );
  protected readonly puedeEliminar = this.loginService.tieneRol(
    this.permisos.eliminar,
  );
  protected readonly puedeConsultar = this.loginService.tieneRol(
    PERMISOS_EXTRA.materialConsultas,
  );
  protected readonly puedeVerReporte = this.loginService.tieneRol(
    PERMISOS_EXTRA.materialReporte,
  );
  /** Docentes y estudiantes ven materiales pero no el directorio de personas (GET /personas) */
  private readonly puedeVerAutores = this.loginService.tieneRol(
    PERMISOS_EXTRA.personasListar,
  );

  protected readonly columnas = [
    'titulo',
    'tipo',
    ...(this.puedeVerAutores ? ['autor'] : []),
    'fechaPublicacion',
    'urlArchivo',
    ...(this.puedeModificar || this.puedeEliminar ? ['acciones'] : []),
  ];
  protected obtenerPagina() {
    return this.mS.pagina(this.page, this.size, this.q).pipe(
      map((p) => ({
        ...p,
        content: p.content.map((m) => ({
          ...m,
          autor:
            [m.apellidosAutor, m.nombreAutor].filter(Boolean).join(', ') ||
            `ID ${m.idPersona}`,
        })),
      })),
    );
  }

  protected eliminar(m: FilaMaterial): void {
    confirmar(this.dialog, {
      titulo: 'Eliminar material',
      mensaje: `¿Seguro que deseas eliminar el material "${m.titulo}"? Esta acción no se puede deshacer.`,
    })
      .pipe(
        filter(Boolean),
        switchMap(() => this.mS.delete(m.idMaterial!)),
      )
      .subscribe({
        next: () => {
          this.snack.open('Material eliminado correctamente.', 'Cerrar', {
            duration: 4000,
          });
          this.cargar();
        },
        error: (err: unknown) => {
          // Sin borrado en cascada: primero hay que quitarlo de los cursos (Materiales por curso)
          const mensaje =
            err instanceof HttpErrorResponse && err.status === 409
              ? 'No se puede eliminar: el material está asociado a cursos. Quítalo primero de "Materiales por curso".'
              : mensajeError(err);
          this.snack.open(mensaje, 'Cerrar', { duration: 6000 });
        },
      });
  }

  protected readonly dataSource = new MatTableDataSource<FilaMaterial>([]);
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
