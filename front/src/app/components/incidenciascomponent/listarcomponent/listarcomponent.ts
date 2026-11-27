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
import { Incidencia } from '../../../models/Incidencia';
import { IncidenciaService } from '../../../services/incidencias.service';
import { ColegioService } from '../../../services/colegios.service';
import { PersonaService } from '../../../services/personas.service';
import { AuthService } from '../../../core/auth.service';
import { confirmar } from '../../compartido/confirmar-dialogo/confirmar-dialogo';
import { mensajeError } from '../../../services/errores';
import { EstadoVacio } from '../../compartido/estado-vacio/estado-vacio';
import { Encabezado } from '../../compartido/encabezado/encabezado';

@Component({
  selector: 'app-incidencias-listar',
  imports: [
    Encabezado,
    EstadoVacio,
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
export class IncidenciaListarComponent implements OnInit {
  private readonly service = inject(IncidenciaService);
  private readonly colegioService = inject(ColegioService);
  private readonly personaService = inject(PersonaService);
  private readonly auth = inject(AuthService);
  private readonly dialog = inject(MatDialog);
  private readonly snack = inject(MatSnackBar);

  readonly dataSource = new MatTableDataSource<Incidencia>([]);
  readonly displayedColumns = [
    'idIncidencia',
    'titulo',
    'fecha',
    'estado',
    'prioridad',
    'idPersona',
    'idColegio',
    'acciones',
  ];
  readonly busqueda = new FormControl('', { nonNullable: true });
  readonly cargando = signal(false);
  readonly error = signal('');
  readonly total = signal(0);
  readonly mapaColegios = signal<Record<number, string>>({});
  readonly mapaPersonas = signal<Record<number, string>>({});
  page = 0;
  size = 10;

  ngOnInit(): void {
    this.cargarCatalogos();
    this.cargar();
  }

  private cargarCatalogos(): void {
    this.colegioService.pagina(0, 100).subscribe({
      next: (p) => {
        const mapa: Record<number, string> = {};
        for (const c of p.content) {
          if (c.idColegio != null) mapa[c.idColegio] = c.nombre;
        }
        this.mapaColegios.set(mapa);
      },
    });

    this.personaService.pagina(0, 100).subscribe({
      next: (p) => {
        const mapa: Record<number, string> = {};
        for (const per of p.content as any[]) {
          if (per.idPersona != null) {
            const nombre = [per.nombres ?? per.nombre, per.apellidos ?? per.apellido]
              .filter(Boolean)
              .join(' ');
            mapa[per.idPersona] = nombre || `Persona #${per.idPersona}`;
          }
        }
        this.mapaPersonas.set(mapa);
      },
    });
  }

  puedeEscribir(): boolean {
    return ['ADMIN', 'ADMIN_ESCUELA', 'ESPECIALISTA', 'LOCAL', 'DOCENTE'].some(
      (rol) => this.auth.roles().includes(rol),
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

  eliminar(dto: Incidencia): void {
    confirmar(this.dialog, {
      titulo: 'Eliminar registro',
      mensaje: '¿Eliminar la incidencia seleccionada?',
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