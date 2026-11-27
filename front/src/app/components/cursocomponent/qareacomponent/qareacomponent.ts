import { ChangeDetectorRef, Component } from '@angular/core';
import { CursoDTOList } from '../../../models/CursoDTOList';
import { Scurso } from '../../../services/scurso';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-qareacomponent',
  imports: [MatSelectModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
  MatInputModule,
FormsModule],
  templateUrl: './qareacomponent.html',
  styleUrl: './qareacomponent.css',
})
export class Qareacomponent {
  dataSource: CursoDTOList[] = [];
  displayedColumns: string[] = ['c1', 'c2', 'c3', 'c4'];

  areaSeleccionada: string = '';

  mensaje: string = '';

  constructor(
    private cS: Scurso,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {}

  buscarporArea(): void {
    if (!this.areaSeleccionada) {
      this.dataSource = [];
      this.mensaje = 'Seleccione un estado';
      return;
    }
    this.cdr.detectChanges();
    this.cS.getArea(this.areaSeleccionada).subscribe({
      next: (data) => {
        this.dataSource = data;
        this.mensaje = data.length === 0 ? 'No se encontraron registros de cursos!!' : '';
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al buscar!!', error);
        this.dataSource = [];
        this.mensaje = 'Error al realizar la búsqueda';
        this.cdr.detectChanges();
      },
    });
  }
}
