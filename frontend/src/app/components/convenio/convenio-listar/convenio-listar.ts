import { OnInit, signal } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { PageEvent } from '@angular/material/paginator';
import { mensajeError as errorPeticion } from '../../../services/errores';
import { CurrencyPipe } from '@angular/common';
import { Encabezado } from '../../compartido/encabezado/encabezado';
import { EstadoVacio } from '../../compartido/estado-vacio/estado-vacio';
import { FechaPePipe } from '../../compartido/formato';
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
import { PaginadorIntl } from '../../compartido/paginador-intl';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { filter, switchMap } from 'rxjs';
import {
  ConvenioDTO,
  ESTADOS_CONVENIO,
  TIPOS_ENTIDAD,
} from '../../../models/convenio';
import { ColegioService } from '../../../services/colegios.service';
import { Convenioservice } from '../../../services/convenioservice';
import { mensajeError } from '../../../services/errores';
import { Loginservice } from '../../../services/loginservice';
import { modulo, PERMISOS_EXTRA } from '../../../services/permisos';
import { confirmar } from '../../compartido/confirmar-dialogo/confirmar-dialogo';

import { Chip } from '../../compartido/chip/chip';
@Component({
  selector: 'app-convenio-listar',
  imports: [
    Chip,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatProgressBarModule,
    MatFormFieldModule,
    MatInputModule,
    RouterLink,
    Encabezado,
    EstadoVacio,
    FechaPePipe,
    CurrencyPipe,
  ],
  providers: [{ provide: MatPaginatorIntl, useClass: PaginadorIntl }],
  templateUrl: './convenio-listar.html',
})
export class ConvenioListar implements OnInit {
  private readonly cS = inject(Convenioservice);
  private readonly colegioService = inject(ColegioService);
  /** Nombre de cada colegio ya consultado, por id (la página solo trae idColegio) */
  protected readonly colegios = signal<Record<number, string | undefined>>({});
  private readonly dialog = inject(MatDialog);
  private readonly snack = inject(MatSnackBar);
  private readonly loginService = inject(Loginservice);

  private readonly permisos = modulo('convenio').permisos;
  protected readonly puedeInsertar = this.loginService.tieneRol(
    this.permisos.insertar,
  );
  protected readonly puedeModificar = this.loginService.tieneRol(
    this.permisos.modificar,
  );
  protected readonly puedeEliminar = this.loginService.tieneRol(
    this.permisos.eliminar,
  );
  protected readonly puedeVerPorVencer = this.loginService.tieneRol(
    PERMISOS_EXTRA.conveniosPorVencer,
  );

  protected readonly tiposEntidad = TIPOS_ENTIDAD;
  protected readonly estados = ESTADOS_CONVENIO;

  protected readonly columnas = [
    'nombre',
    'entidad',
    'tipoEntidad',
    'region',
    'colegio',
    'fechaInicio',
    'fechaFin',
    'presupuesto',
    'estado',
    ...(this.puedeModificar || this.puedeEliminar ? ['acciones'] : []),
  ];
  protected obtenerPagina() {
    return this.cS.pagina(this.page, this.size, this.q);
  }
  protected filtrar(texto: string): void {
    this.buscar(texto);
  }

  protected claseEstado(estado: string | null | undefined): string {
    if (estado === 'VIGENTE') return 'estado-ok';
    if (estado === 'VENCIDO' || estado === 'EN_RENOVACION')
      return 'estado-alerta';
    return 'estado-neutro';
  }

  protected eliminar(c: ConvenioDTO): void {
    confirmar(this.dialog, {
      titulo: 'Eliminar convenio',
      mensaje: `¿Seguro que deseas eliminar el convenio "${c.nombre}"? Esta acción no se puede deshacer.`,
    })
      .pipe(
        filter(Boolean),
        switchMap(() => this.cS.delete(c.idConvenio!)),
      )
      .subscribe({
        next: () => {
          this.snack.open('Convenio eliminado correctamente.', 'Cerrar', {
            duration: 4000,
          });
          this.cargar();
        },
        error: (err: unknown) =>
          this.snack.open(mensajeError(err), 'Cerrar', { duration: 6000 }),
      });
  }

  protected readonly dataSource = new MatTableDataSource<ConvenioDTO>([]);
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
        this.cargarColegios(p.content);
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
  /** Consulta el nombre de los colegios de la página que aún no se conocen */
  private cargarColegios(convenios: ConvenioDTO[]): void {
    const ids = new Set(
      convenios
        .map((c) => c.idColegio)
        .filter((id): id is number => !!id && !(id in this.colegios())),
    );
    for (const id of ids) {
      this.colegioService.listId(id).subscribe({
        next: (r) => this.colegios.update((m) => ({ ...m, [id]: r.nombre })),
        // Si falla, la celda sigue mostrando el id
        error: () => undefined,
      });
    }
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
