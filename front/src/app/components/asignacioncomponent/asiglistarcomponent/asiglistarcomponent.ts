import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { AsignacionDTOList } from '../../../models/AsignacionDTOList';
import { Sasignacion } from '../../../services/sasignacion';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-asiglistarcomponent',
  imports: [MatTableModule, MatButtonModule, MatIconModule, RouterLink],
  templateUrl: './asiglistarcomponent.html',
  styleUrl: './asiglistarcomponent.css',
})
export class Asiglistarcomponent implements OnInit {
  displayedColumns: string[] = ['c1', 'c2', 'c3', 'c4', 'c5', 'c6', 'c7', 'c8', 'c9', 'c10'];
  dataSource: AsignacionDTOList[] = [];
  constructor(
    private cdr: ChangeDetectorRef,
    private aS: Sasignacion,
  ) {}

  ngOnInit(): void {}

  obtenerasignacion() {
    this.aS.getListAsignacion().subscribe({
      next: (data) => {
        this.dataSource = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.log('Ocurrió un error!!', err);
        this.cdr.detectChanges();
      },
    });
  }

  eliminarasignacion(id: number) {
    this.aS.deleteAsignacion(id).subscribe({
      next: () => {
        this.obtenerasignacion();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.log('Ocurrió un error!!', err);
        this.cdr.detectChanges();
      },
    });
  }
}

