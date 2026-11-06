import { MaterialDTO } from '../../../models/material';
import { Materialservice } from '../../../services/materialservice';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { OnInit, signal, Component, inject } from '@angular/core';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import {
  PageEvent,
  MatPaginatorIntl,
  MatPaginatorModule,
} from '@angular/material/paginator';
import {
  mensajeError as errorPeticion,
  mensajeError,
} from '../../../services/errores';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { filter, switchMap } from 'rxjs';
import { MaterialCursoDTO } from '../../../models/materialcurso';
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
    ReactiveFormsModule,
    MatInputModule,
    MatTableModule,
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
export class MaterialcursoListar implements OnInit {
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
    this.cargarIdMaterial();
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

  protected readonly dataSource = new MatTableDataSource<MaterialCursoDTO>([]);
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
  private readonly servicioIdMaterial = inject(Materialservice);
  protected readonly listaIdMaterial = signal<MaterialDTO[]>([]);
  protected readonly buscarIdMaterial = new FormControl('', {
    nonNullable: true,
  });
  protected cargarIdMaterial(): void {
    this.servicioIdMaterial
      .pagina(0, 20, this.buscarIdMaterial.value.trim())
      .subscribe({ next: (p) => this.listaIdMaterial.set(p.content) });
  }
}
