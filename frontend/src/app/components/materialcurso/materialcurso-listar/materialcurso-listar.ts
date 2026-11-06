import { FormControl } from '@angular/forms';
import { ReferenciaComponent } from '../../../shared/referencia.component';
import { PaginaEdissonBase } from '../../../shared/pagina-edisson.base';
import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import {
  MatPaginatorIntl,
  MatPaginatorModule,
} from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { filter, switchMap } from 'rxjs';
import { MaterialCursoDTO } from '../../../models/materialcurso';
import { mensajeError } from '../../../services/errores';
import { Loginservice } from '../../../services/loginservice';
import { Materialcursoservice } from '../../../services/materialcursoservice';
import { modulo, PERMISOS_EXTRA } from '../../../services/permisos';
import { confirmar } from '../../compartido/confirmar-dialogo/confirmar-dialogo';
import { PaginadorIntl } from '../../compartido/paginador-intl';

interface FilaMaterialCurso extends MaterialCursoDTO {
  material: string;
  curso: string;
}

/** Relación material-curso: el backend solo permite asociar y quitar (no modificar) */
@Component({
  selector: 'app-materialcurso-listar',
  imports: [
    MatTableModule,
    ReferenciaComponent,
    MatPaginatorModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressBarModule,
    MatFormFieldModule,
    MatSelectModule,
    RouterLink,
  ],
  providers: [{ provide: MatPaginatorIntl, useClass: PaginadorIntl }],
  templateUrl: './materialcurso-listar.html',
})
export class MaterialcursoListar extends PaginaEdissonBase<MaterialCursoDTO> {
  private readonly mcS = inject(Materialcursoservice);
  private readonly dialog = inject(MatDialog);
  private readonly snack = inject(MatSnackBar);
  private readonly loginService = inject(Loginservice);

  private readonly permisos = modulo('materialcurso').permisos;
  protected readonly puedeInsertar = this.loginService.tieneRol(
    this.permisos.insertar,
  );
  protected readonly puedeEliminar = this.loginService.tieneRol(
    this.permisos.eliminar,
  );
  protected readonly puedeVerReporte = this.loginService.tieneRol(
    PERMISOS_EXTRA.materialCursoReporte,
  );

  protected readonly idMaterial = new FormControl<number | null>(null);
  protected readonly columnas = [
    'material',
    'curso',
    ...(this.puedeEliminar ? ['acciones'] : []),
  ];
  protected obtenerPagina() {
    return this.mcS.pagina(
      this.page,
      this.size,
      this.q,
      this.idMaterial.value ?? undefined,
    );
  }
  constructor() {
    super();
    this.idMaterial.valueChanges.subscribe(() => {
      this.page = 0;
      this.cargar();
    });
  }
  protected limpiarFiltro() {
    this.idMaterial.setValue(null);
  }

  protected quitar(r: FilaMaterialCurso): void {
    confirmar(this.dialog, {
      titulo: 'Quitar material del curso',
      mensaje: `¿Quitar "${r.material}" del curso "${r.curso}"? El material no se elimina, solo deja de estar asociado.`,
      confirmar: 'Quitar',
    })
      .pipe(
        filter(Boolean),
        switchMap(() => this.mcS.delete(r.idMaterial, r.idCurso)),
      )
      .subscribe({
        next: () => {
          this.snack.open('Material quitado del curso.', 'Cerrar', {
            duration: 4000,
          });
          this.cargar();
        },
        error: (err: unknown) =>
          this.snack.open(mensajeError(err), 'Cerrar', { duration: 6000 }),
      });
  }
}
